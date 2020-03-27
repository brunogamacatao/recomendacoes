package br.brunocatao.recomendacoes;

import lombok.Data;
import lombok.NonNull;

@Data
public class Avaliacao {
  @NonNull
  private Filme filme;
  @NonNull
  private double nota;
}
