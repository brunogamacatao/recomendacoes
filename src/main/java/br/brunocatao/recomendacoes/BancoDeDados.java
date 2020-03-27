package br.brunocatao.recomendacoes;

import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
public class BancoDeDados {
  private static Map<String, Filme> filmes = new HashMap<>();
  private static Map<String, Usuario> usuarios = new HashMap<>();

  private static BancoDeDados instance;

  public static BancoDeDados getInstance() {
    if (instance == null) {
      instance = new BancoDeDados();
    }

    return instance;
  }

  public BancoDeDados() {
    criaFilmes();
    criaUsuarios();
  }

  // Métodos de criação
  private void criaFilmes() {
    adicionaFilme("Lady in the Water");
    adicionaFilme("Snakes on a Plane");
    adicionaFilme("Just My Luck");
    adicionaFilme("Superman Returns");
    adicionaFilme("You, Me and Dupree");
    adicionaFilme("The Night Listener");
  }

  private void adicionaFilme(String nome) {
    filmes.put(nome, new Filme(nome));
  }

  private void addUsuario(Usuario usuario) {
    usuarios.put(usuario.getNome(), usuario);
  }

  private void criaUsuarios() {
    addUsuario(AvaliacaoBuilder.create("Lisa Rose")
        .add("Lady in the Water", 2.5)
        .add("Snakes on a Plane", 3.5)
        .add("Just My Luck", 3.0)
        .add("Superman Returns", 3.5)
        .add("You, Me and Dupree", 2.5)
        .add("The Night Listener", 3.0)
        .build());
    addUsuario(AvaliacaoBuilder.create("Gene Seymour")
        .add("Lady in the Water", 3.0)
        .add("Snakes on a Plane", 3.5)
        .add("Just My Luck", 1.5)
        .add("Superman Returns", 5.0)
        .add("You, Me and Dupree", 3.5)
        .add("The Night Listener", 3.0)
        .build());
    addUsuario(AvaliacaoBuilder.create("Michael Phillips")
        .add("Lady in the Water", 2.5)
        .add("Snakes on a Plane", 3.0)
        .add("Superman Returns", 3.5)
        .add("The Night Listener", 4.0)
        .build());
    addUsuario(AvaliacaoBuilder.create("Claudia Puig")
        .add("Snakes on a Plane", 3.5)
        .add("Just My Luck", 3.0)
        .add("Superman Returns", 4.0)
        .add("You, Me and Dupree", 2.5)
        .add("The Night Listener", 4.5)
        .build());
    addUsuario(AvaliacaoBuilder.create("Mick LaSalle")
        .add("Lady in the Water", 3.0)
        .add("Snakes on a Plane", 4.0)
        .add("Just My Luck", 2.0)
        .add("Superman Returns", 3.0)
        .add("You, Me and Dupree", 2.0)
        .add("The Night Listener", 3.0)
        .build());
    addUsuario(AvaliacaoBuilder.create("Jack Matthews")
        .add("Lady in the Water", 3.0)
        .add("Snakes on a Plane", 4.0)
        .add("Superman Returns", 5.0)
        .add("You, Me and Dupree", 3.5)
        .add("The Night Listener", 3.0)
        .build());
    addUsuario(AvaliacaoBuilder.create("Toby")
        .add("Snakes on a Plane", 4.5)
        .add("Superman Returns", 4.0)
        .add("You, Me and Dupree", 1.0)
        .build());
  }

  // Padrão de projeto Builder
  private static class AvaliacaoBuilder {
    private Usuario usuario;

    private AvaliacaoBuilder(Usuario usuario) {
      this.usuario = usuario;
    }

    public static AvaliacaoBuilder create(String nomeUsuario) {
      AvaliacaoBuilder builder = new AvaliacaoBuilder(new Usuario(nomeUsuario));
      return builder;
    }

    public AvaliacaoBuilder add(String filme, double nota) {
      this.usuario.getAvaliacoes().put(filme, new Avaliacao(filmes.get(filme), nota));
      return this;
    }

    public Usuario build() {
      return this.usuario;
    }
  }

  // Métodos utilitários
  public Collection<Usuario> getUsuarios() {
    return usuarios.values();
  }

  public Usuario getUsuario(String nome) {
    return usuarios.get(nome);
  }

  public Filme getFilme(String nome) {
    return filmes.get(nome);
  }

  public double getNota(String usuario, String filme) {
    return usuarios.get(usuario).getAvaliacoes().get(filme).getNota();
  }

  public static void main(String[] args) {
    System.out.println(BancoDeDados.getInstance().getNota("Lisa Rose", "Lady in the Water"));
  }
}
