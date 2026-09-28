package com.idea_forge.modules.idea.enums;

public enum IdeaStatus {

    DRAFT("Rascunho"),
    VALIDATION("Validação"),
    DEVELOPMENT("Desenvolvimento"),
    LAUNCHED("Lançadas");

    private final String descricao;

    IdeaStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
