package br.brunocatao.recomendacoes;

import lombok.Data;
import lombok.NonNull;

@Data
public class Filme {
  @NonNull
  private String nome;
}
