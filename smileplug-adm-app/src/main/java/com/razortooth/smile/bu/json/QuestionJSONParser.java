package com.razortooth.smile.bu.json;

import java.util.Map;

import org.json.JSONObject;

import com.razortooth.smile.domain.Question;
import com.razortooth.smile.domain.Student;

public class QuestionJSONParser {

    private static final String IP = "IP";
    private static final String QUESTION = "Q";
    private static final String OPTION_1 = "O1";
    private static final String OPTION_2 = "O2";
    private static final String OPTION_3 = "O3";
    private static final String OPTION_4 = "O4";
    private static final String ANSWER = "A";
    private static final String IMAGE = "PIC";

    public static final Question process(int number, JSONObject object,
        Map<String, Student> students) {

        String ip = object.optString(IP);

        Student owner = students.get(ip);
        owner.setMade(true);

        String question = object.optString(QUESTION);
        String o1 = object.optString(OPTION_1);
        String o2 = object.optString(OPTION_2);
        String o3 = object.optString(OPTION_3);
        String o4 = object.optString(OPTION_4);
        int answer = Integer.valueOf(object.optString(ANSWER));
        String image = object.optString(IMAGE);

        return new Question(number, owner.getName(), question, o1, o2, o3, o4, answer, image);

    }

}