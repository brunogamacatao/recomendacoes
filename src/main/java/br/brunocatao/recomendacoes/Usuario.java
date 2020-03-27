package br.brunocatao.recomendacoes;

import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Data
public class Usuario {
  @NonNull
  private String nome;
  private Map<String, Avaliacao> avaliacoes = new HashMap<>();

  public double getNota(String filme) {
    return avaliacoes.get(filme).getNota();
  }
}
