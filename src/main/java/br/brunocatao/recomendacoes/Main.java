package br.brunocatao.recomendacoes;

public class Main {
  public static void main(String[] args) {
    Usuario toby = BancoDeDados.getInstance().getUsuario("Toby");

    System.out.println("Usuários parecidos com Toby");
    Recomendador.getUsuariosSimilares(toby).forEach(s -> {
      System.out.println(s.getUsuario().getNome() + " - " + s.getScore());
    });

    System.out.println("\nFilmes recomendados para Toby");
    Recomendador.getFilmesRecomendados(toby).forEach(s -> {
      System.out.println(s.getFilme().getNome() + " - " + s.getScore());
    });

    System.out.println("\nFilmes similares a Superman Returns");
    Recomendador.getFilmesSimilares(BancoDeDados.getInstance().getFilme("Superman Returns")).forEach(s -> {
      System.out.println(s.getFilme().getNome() + " - " + s.getScore());
    });
  }
}
