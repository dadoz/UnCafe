package com.application.material.takeacoffee.app.models;

import static com.application.material.takeacoffee.app.models.ReviewStatus.ReviewStatusEnum;

import java.util.ArrayList;

/**
 * Created by davide on 11/09/14.
 */
public class ReviewCounter {
    private String key;
    private ArrayList<ReviewCounterTimestamp> reviewCounterTimestampList;
    private int REVIEW_COUNTER_ERROR = -1;

    public ReviewCounter(String key, long toTimestamp, int good, int notsobad, int worst) {
        this.key = key;
        this.reviewCounterTimestampList = new ArrayList<ReviewCounterTimestamp>();
        addCounters(toTimestamp, good, notsobad, worst);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCounterByParams(long toTimestamp, ReviewStatusEnum status) {
        ReviewCounterTimestamp reviewCounterTimestamp = getReviewCounterTimestamp(toTimestamp);
        if(reviewCounterTimestamp == null) {
            return REVIEW_COUNTER_ERROR;
        }

        switch (status) {
            case GOOD:
                return reviewCounterTimestamp.getGood();
            case NOTSOBAD:
                return reviewCounterTimestamp.getNotsobad();
            case WORST:
                return reviewCounterTimestamp.getWorst();
        }
        return REVIEW_COUNTER_ERROR;
    }

    private ReviewCounterTimestamp getReviewCounterTimestamp(long toTimestamp) {
        for (ReviewCounterTimestamp cnt : reviewCounterTimestampList) {
            if (cnt.getTimestampKey() == toTimestamp) {
                return cnt;
            }
        }
        return null;
    }

    public void addCounters(long toTimestamp, int good, int notsobad, int worst) {
        this.reviewCounterTimestampList.add(new ReviewCounterTimestamp(toTimestamp, good, notsobad, worst));
    }

    public boolean setCounters(long toTimestamp, int good, int notsobad, int worst) {
        ReviewCounterTimestamp reviewCounterTimestamp = getReviewCounterTimestamp(toTimestamp);
        if(reviewCounterTimestamp == null) {
            return false;
        }
        reviewCounterTimestamp.setCounters(good, notsobad, worst);
        return true;
    }

    static class ReviewCounterTimestamp {
        long timestampKey;

        long toTimestamp;
        long fromTimestamp;

        StatusCounter goodStatusCnt;
        StatusCounter notsobadStatusCnt;
        StatusCounter worstStatusCnt;

        public ReviewCounterTimestamp(long toTimestamp, int good, int notsobad, int worst) {
            this.timestampKey = toTimestamp;
            this.toTimestamp = toTimestamp;
            setCounters(good, notsobad, worst);
        }

        public void setCounters(int good, int notsobad, int worst) {
            this.goodStatusCnt = new StatusCounter(ReviewStatusEnum.GOOD, good);
            this.notsobadStatusCnt = new StatusCounter(ReviewStatusEnum.NOTSOBAD, notsobad);
            this.worstStatusCnt = new StatusCounter(ReviewStatusEnum.WORST, worst);
        }

        private int getGood() {
            return this.goodStatusCnt.getCnt();
        }

        private int getNotsobad() {
            return this.notsobadStatusCnt.getCnt();
        }

        private int getWorst() {
            return this.worstStatusCnt.getCnt();
        }

        public long getTimestampKey() {
            return timestampKey;
        }
    }

    static class StatusCounter {
        private ReviewStatusEnum status;
        private int cnt;

        public StatusCounter(ReviewStatusEnum status, int cnt) {
            this.status = status;
            this.cnt = cnt;
        }

        public int getCnt() {
            return this.cnt;
        }

        public ReviewStatusEnum getReviewStatus() {
            return this.status;
        }
    }
}
