package br.brunocatao.recomendacoes;

import lombok.Data;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class Recomendador {
  @Data
  public static class ScoreUsuario {
    @NonNull private Usuario usuario;
    @NonNull private double score;
  }

  @Data
  public static class ScoreFilme {
    @NonNull private Filme filme;
    @NonNull private double score;
  }

  public static List<ScoreUsuario> getUsuariosSimilares(Usuario usuario, CalculadorDeSimilaridadeIF calculador) {
    return BancoDeDados.getInstance().getUsuarios().stream() // para todos os usuários u
        .filter(u -> u != usuario) // diferentes do usuário passado como argumento
        .map(u -> new ScoreUsuario(u, calculador.calcula(usuario, u))) // calcula a similaridade
        .sorted((s1, s2) -> Double.compare(s2.getScore(), s1.getScore())) // ordena da maior para menor similaridade
        .collect(Collectors.toList());
  }

  public static List<ScoreUsuario> getUsuariosSimilares(Usuario usuario) {
    return getUsuariosSimilares(usuario, CalculadorDeSimilaridade::getSimilaridadePearson);
  }

  public static List<ScoreFilme> getFilmesRecomendados(Usuario usuario, CalculadorDeSimilaridadeIF calculador) {
    Map<String, Double> totalPorFilme = new HashMap<>();
    Map<String, Double> similaridadePorFilme = new HashMap<>();

    BancoDeDados.getInstance().getUsuarios().stream() // para todos os usuários u
        .filter(u -> u != usuario) // diferentes do usuário passado como argumento
        .map(u -> new ScoreUsuario(u, calculador.calcula(usuario, u))) // calcula a similaridade
        .filter(s -> s.getScore() > 0) // remove os usuários que não tem similaridade  entre si
        .forEach(s -> {
          Set<String> filmesQueEuNaoVi = new HashSet<>(s.getUsuario().getAvaliacoes().keySet());
          filmesQueEuNaoVi.removeAll(usuario.getAvaliacoes().keySet());

          filmesQueEuNaoVi.forEach(filme -> {
            double tpf = totalPorFilme.containsKey(filme) ? totalPorFilme.get(filme) : 0.0;
            double spf = similaridadePorFilme.containsKey(filme) ? similaridadePorFilme.get(filme) : 0.0;

            tpf += s.getUsuario().getNota(filme) * s.getScore();
            spf += s.getScore();

            totalPorFilme.put(filme, tpf);
            similaridadePorFilme.put(filme, spf);
          });
        });

    return totalPorFilme.keySet().stream()
        .map(nome -> {
          double score = totalPorFilme.get(nome) / similaridadePorFilme.get(nome);
          return new ScoreFilme(BancoDeDados.getInstance().getFilme(nome), score);
        })
        .sorted((s1, s2) -> Double.compare(s2.getScore(), s1.getScore()))
        .collect(Collectors.toList());
  }

  public static List<ScoreFilme> getFilmesRecomendados(Usuario usuario) {
    return getFilmesRecomendados(usuario, CalculadorDeSimilaridade::getSimilaridadePearson);
  }

  public static List<ScoreFilme> getFilmesSimilares(Filme filme) {
    // Tenho que criar uma tabela avaliação inversa => a nota do usuário a partir do filme
    Map<String, Map<String, Double>> scoreUsuariosPorFilme = new HashMap<>();
    BancoDeDados.getInstance().getUsuarios().forEach(u -> {
      u.getAvaliacoes().keySet().forEach(nomeFilme -> {
        if (!scoreUsuariosPorFilme.containsKey(nomeFilme)) {
          scoreUsuariosPorFilme.put(nomeFilme, new HashMap<>());
        }
        scoreUsuariosPorFilme.get(nomeFilme).put(u.getNome(), u.getNota(nomeFilme));
      });
    });

    // Agora, posso usar a mesma lógica para comparar os filmes entre si
    return scoreUsuariosPorFilme.keySet().stream() // para todos os filmes
        .map(nomeFilme -> BancoDeDados.getInstance().getFilme(nomeFilme))
        .filter(f -> f != filme) // diferentes do filme passado como argumento
        .map(f -> new ScoreFilme(f, similaridadePearsonEntreFilmes(f, filme, scoreUsuariosPorFilme)))
        .sorted((s1, s2) -> Double.compare(s2.getScore(), s1.getScore()))
        .collect(Collectors.toList());
  }

  private static double similaridadePearsonEntreFilmes(Filme f1, Filme f2, Map<String, Map<String, Double>> scoreUsuariosPorFilme) {
    Set<String> usuariosQueViramAmbosOsFilmes = new HashSet<>(scoreUsuariosPorFilme.get(f1.getNome()).keySet());
    usuariosQueViramAmbosOsFilmes.retainAll(scoreUsuariosPorFilme.get(f2.getNome()).keySet());

    // Se dois usuários não viram esse filme
    if (usuariosQueViramAmbosOsFilmes.isEmpty()) {
      return 0.0; // não há como calcular similaridade entre eles
    }

    // Soma todas as preferências
    double soma1 = usuariosQueViramAmbosOsFilmes.stream().map(u -> scoreUsuariosPorFilme.get(f1.getNome()).get(u)).reduce(0.0, Double::sum);
    double soma2 = usuariosQueViramAmbosOsFilmes.stream().map(u -> scoreUsuariosPorFilme.get(f2.getNome()).get(u)).reduce(0.0, Double::sum);

    // Soma dos quadrados
    double somaQ1 = usuariosQueViramAmbosOsFilmes.stream().map(u -> scoreUsuariosPorFilme.get(f1.getNome()).get(u)).map(n -> n * n).reduce(0.0, Double::sum);
    double somaQ2 = usuariosQueViramAmbosOsFilmes.stream().map(u -> scoreUsuariosPorFilme.get(f2.getNome()).get(u)).map(n -> n * n).reduce(0.0, Double::sum);

    // Soma dos produtos
    double somaP = usuariosQueViramAmbosOsFilmes.stream().map(u -> scoreUsuariosPorFilme.get(f1.getNome()).get(u) * scoreUsuariosPorFilme.get(f2.getNome()).get(u)).reduce(0.0, Double::sum);

    // Calcula o Score de Pearson
    int n = usuariosQueViramAmbosOsFilmes.size();
    double numerador = somaP - (soma1 * soma2 / n);
    double denominador = Math.sqrt((somaQ1-Math.pow(soma1,2)/n)*(somaQ2-Math.pow(soma2,2)/n));

    if (denominador == 0.0) {
      return 0.0;
    }

    return numerador / denominador;
  }
}
