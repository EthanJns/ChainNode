package com.evote.Chain;

public class BallotBlock {
    private String ballotHash;
    private String ballotId;
    private String ballotDescription;
    private String previousHash;
    private String ballotDecision;
    private Long timeStamp;
    // Will I need a nonce?
    private Integer nonce = 0;

    public BallotBlock(String ballotId, String ballotDescription, String ballotDecision, String previousHash,
            Long timeStamp,
            String ballotHash) {
        this.ballotId = ballotId;
        this.ballotDescription = ballotDescription;
        this.ballotDecision = ballotDecision;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.ballotHash = ballotHash;
    }

    public BallotBlock(String delimterHeldChainInfo) {
        String[] chainInfo = delimterHeldChainInfo.split("~");
        this.ballotHash = chainInfo[0];
        this.ballotId = chainInfo[1];
        this.ballotDescription = chainInfo[2];
        this.ballotDecision = chainInfo[3];
        this.previousHash = chainInfo[4];
        this.timeStamp = Long.parseLong(chainInfo[5]);
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public String getHash() {
        return this.ballotHash;
    }

    public Long getTimeStamp() {
        return this.timeStamp;
    }

    public String stringify() {
        return this.ballotHash + "~" + this.ballotId + "~" + this.ballotDescription + "~" + this.ballotDecision + "~"
                + this.previousHash + "~" + this.timeStamp.toString();
    }
}
