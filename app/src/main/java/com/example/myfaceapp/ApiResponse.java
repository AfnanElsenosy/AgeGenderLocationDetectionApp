package com.example.myfaceapp;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponse {
    @SerializedName("gender")
    private Gender gender;

    @SerializedName("age")
    private double age;

    @SerializedName("expression")
    private List<Expression> expressions;

    @SerializedName("age_group")
    private String ageGroup;

    @SerializedName("rectangle")
    private Rectangle rectangle;

    public Gender getGender() {
        return gender;
    }

    public double getAge() {
        return age;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public static class Gender {
        @SerializedName("value")
        private String value;

        @SerializedName("probability")
        private double probability;

        public String getValue() {
            return value;
        }

        public double getProbability() {
            return probability;
        }
    }

    public static class Expression {
        @SerializedName("value")
        private String value;

        @SerializedName("probability")
        private double probability;

        public String getValue() {
            return value;
        }

        public double getProbability() {
            return probability;
        }
    }

    public static class Rectangle {
        @SerializedName("left")
        private int left;

        @SerializedName("top")
        private int top;

        @SerializedName("right")
        private int right;

        @SerializedName("bottom")
        private int bottom;

        public int getLeft() {
            return left;
        }

        public int getTop() {
            return top;
        }

        public int getRight() {
            return right;
        }

        public int getBottom() {
            return bottom;
        }
    }

}
