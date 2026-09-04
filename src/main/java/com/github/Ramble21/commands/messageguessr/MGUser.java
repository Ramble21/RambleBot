package com.github.Ramble21.commands.messageguessr;

import org.jetbrains.annotations.NotNull;

public record MGUser(long idLong, String username, String effectiveName, String globalName) {

    @Override
    public @NotNull String toString() {
        return "idLong: " + idLong + ",username= " + username + ",effectiveName= " + effectiveName + ",globalName= " + globalName;
    }

}

