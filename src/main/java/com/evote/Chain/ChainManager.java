package com.evote.Chain;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.evote.Chain.BallotBlock;

public class ChainManager {
    private static final String chainDirectory = "./src/main/resources/chains/";
    private static final String ALGORITHM = "AES";

    public List<BallotBlock> retrieveChain(String ballotId) {
        List<BallotBlock> ballotChain = new ArrayList<>();
        final String chainLocation = chainDirectory + ballotId;
        try {
            File chainFile = new File(chainLocation);
            if (!chainFile.createNewFile()) {
                FileInputStream chainIn = new FileInputStream(chainFile);
                BufferedInputStream chainBuf = new BufferedInputStream(chainIn);
                byte[] encodedChainBytes = chainBuf.readAllBytes();
                String chainString = new String(encodedChainBytes, StandardCharsets.UTF_8);
                String[] chainStringList = chainString.split("\\|\\|\\|\\|");
                for (String blockString : chainStringList) {
                    ballotChain.add(new BallotBlock(decrypt(blockString)));
                }
                chainBuf.close();
            } else {
                throw new IOException("File should already exist");
            }
        } catch (IOException ex) {

        }
        return ballotChain;
    }

    public void writeChain(String ballotId, BallotBlock ballotChain) {
        final String chainLocation = chainDirectory + ballotId;
        try {
            File chainFile = new File(chainLocation);
            Boolean isNewFile = chainFile.createNewFile();
            FileOutputStream chainOut = new FileOutputStream(chainFile, !isNewFile);
            BufferedOutputStream chainBuf = new BufferedOutputStream(chainOut);
            StringBuilder chainBuilder = new StringBuilder();
            chainBuilder.append(encrypt(ballotChain.stringify()) + "||||");
            byte[] chainBytes = chainBuilder.toString().getBytes();
            chainBuf.write(chainBytes);
            chainBuf.close();
        } catch (Exception ex) {
            System.out.println("");
        }
    }

    public String encrypt(String blockString) {
        try {
            MessageDigest sha = null;
            sha = MessageDigest.getInstance("SHA-1");
            byte[] secretKey = "AsTheMomentStandsThisIsMySecretKey, I will generate one per blockchain".getBytes();
            secretKey = sha.digest(secretKey);
            secretKey = Arrays.copyOf(secretKey, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(blockString.getBytes("UTF-8")));
        } catch (Exception ex) {
            // Failed encryption for some reason
            System.out.println(1);
        }
        return "FOSAIHBFIUSAHFSAUF*PA(S*)(AS";
    }

    public String decrypt(String encryptedBlock) {
        try {
            MessageDigest sha = null;
            sha = MessageDigest.getInstance("SHA-1");
            byte[] secretKey = "AsTheMomentStandsThisIsMySecretKey, I will generate one per blockchain".getBytes();
            secretKey = sha.digest(secretKey);
            secretKey = Arrays.copyOf(secretKey, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedBlock)));
        } catch (Exception ex) {
            System.out.println(1);
        }

        return "HFAJFHSA:IFHNASFA:S";
    }

    public Boolean verifySingleBlock(List<BallotBlock> validationChain, BallotBlock ballotBlock,
            List<String> votingHashes) {
        int index = validationChain.indexOf(ballotBlock);
        String previousHash = validationChain.get(index - 1).getHash();
        String validVotingHash = null;

        for (String hash : votingHashes) {
            String[] hashElements = new String[] { validationChain.get(index - 1).getHash(), hash };
            if (ballotBlock.getHash()
                    .matches(calculateHash(hashElements))) {
                validVotingHash = hash;
                votingHashes.remove(hash);
                break;
            }
        }
        return validVotingHash != null && ballotBlock.getPreviousHash().matches(previousHash);
    }

    public boolean validateChain(String ballotId, String ballotDescription, List<String> votingHashes)
            throws InterruptedException {
        List<BallotBlock> validationChain = this.retrieveChain(ballotId);
        List<BallotBlock> stillNeedsValidation = new ArrayList();
        String[] initialHashElements = new String[] { ballotId, ballotDescription };
        String initialBallotHash = this.calculateHash(initialHashElements);
        if (initialBallotHash.matches(validationChain.get(0).getHash())) {
            if (validationChain.size() == 1) {
                return true;
            }
            for (int i = 1; i < validationChain.size(); i++) {
                String previousHash = validationChain.get(i - 1).getHash();
                BallotBlock currentBlock = validationChain.get(i);
                String validVotingHash = null;
                for (String votingHash : votingHashes) {
                    String[] hashElements = new String[] { validationChain.get(i - 1).getHash(), votingHash };
                    if (currentBlock.getHash().matches(calculateHash(hashElements))) {
                        validVotingHash = votingHash;
                        votingHashes.remove(votingHash);
                        break;
                    }
                }
                if (validVotingHash == null || !currentBlock.getPreviousHash().matches(previousHash)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String calculateHash(String[] hashElements) {
        MessageDigest digest = null;
        String dataBeingHashed = "";
        for (String datum : hashElements) {
            dataBeingHashed += datum.strip();
        }
        byte[] bytes = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataBeingHashed.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            System.out.println("Error occured, we need to set up logging");
        }

        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    private boolean allThreadsAlive(List<Thread> threads) {
        boolean allAlive = true;
        for (Thread t : threads) {
            allAlive &= !t.isAlive();
        }

        return allAlive;
    }
}
