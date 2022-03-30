/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

import java.util.Map;

public class CommunityPoll {

    private final int id;
    private final String question;
    private final Map<Integer, PollAnswer> answers;
    private final Map<Integer, Long> responses;
    private final long endTimestamp;

    public CommunityPoll(int id, String question, Map<Integer, PollAnswer> answers, Map<Integer, Long> responses, long endTimestamp) {
        this.id = id;
        this.question = question;
        this.answers = answers;
        this.responses = responses;
        this.endTimestamp = endTimestamp;
    }

    public int getId() {
        return id;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public Map<Integer, Long> getResponses() {
        return responses;
    }

    public String getQuestion() {
        return question;
    }

    public Map<Integer, PollAnswer> getAnswers() {
        return answers;
    }

    public static class PollAnswer {

        private final int id;
        private final String answer;

        public PollAnswer(int id, String answer) {
            this.id = id;
            this.answer = answer;
        }

        public int getId() {
            return id;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
