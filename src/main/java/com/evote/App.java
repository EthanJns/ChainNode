package com.evote;

import java.util.Scanner;

import main.java.com.evote.Chain.ChainManager;
import main.java.com.evote.NodeServer.NodeServer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        BallotBlock ballotBlock = new BallotBlock("String ballotId", "String ballotDescription",
                "String ballotDecision", "String previousHash",
                10L, "String ballotHash");
        ChainManager chainManager = new ChainManager();
        System.out.println(1);
    }
}