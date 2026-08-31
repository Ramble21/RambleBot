package com.github.Ramble21.commands.messageguessr;

public class MessageGuessrOptions {
    private int timeCutoff;
    private int numWrongAnswers;
    private boolean hideOldMembers;
    private boolean useNicknames;

    public MessageGuessrOptions(int timeCutoff, int numWrongAnswers, boolean hideOldMembers, boolean useNicknames) {
        this.timeCutoff = timeCutoff;
        this.numWrongAnswers = numWrongAnswers;
        this.hideOldMembers = hideOldMembers;
        this.useNicknames = useNicknames;
    }

    public int getTimeCutoff() {
        return timeCutoff;
    }

    public void setTimeCutoff(int timeCutoff) {
        this.timeCutoff = timeCutoff;
    }

    public int getNumWrongAnswers() {
        return numWrongAnswers;
    }

    public void setNumWrongAnswers(int numWrongAnswers) {
        this.numWrongAnswers = numWrongAnswers;
    }

    public boolean hidesOldMembers() {
        return hideOldMembers;
    }

    public void setHideOldMembers(boolean hideOldMembers) {
        this.hideOldMembers = hideOldMembers;
    }

    public boolean usesNicknames() {
        return useNicknames;
    }

    public void setUseNicknames(boolean useNicknames) {
        this.useNicknames = useNicknames;
    }

}