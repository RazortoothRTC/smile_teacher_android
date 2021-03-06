/**
Copyright 2012-2013 SMILE Consortium, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
**/
package org.smilec.smile.domain;

public class ServerQuestionWrapper {

    private String NAME;
    private String Q;
    private String O1;
    private String O2;
    private String O3;
    private String O4;
    private int A;
    private String PICURL;
    private String TYPE;
    private String IP;

    public ServerQuestionWrapper(Question question) {
        super();
        this.NAME = question.getOwner();
        this.IP = question.getIp();
        this.Q = question.getQuestion();
        this.O1 = question.getOption1();
        this.O2 = question.getOption2();
        this.O3 = question.getOption3();
        this.O4 = question.getOption4();
        this.A = question.getAnswer();
        if (question.hasImage()) {
            this.PICURL = question.getImageUrl();
            this.TYPE = "QUESTION_PIC";
        } else {
            this.TYPE = "QUESTION";
        }
    }

    public String getNAME() { return NAME; }
    public void setNAME(String name) { NAME = name; }

    public String getQ() { return Q; }
    public void setQ(String q) { Q = q; }

    public String getO1() { return O1; }
    public void setO1(String o1) { O1 = o1; }

    public String getO2() { return O2; }
    public void setO2(String o2) { O2 = o2; }

    public String getO3() { return O3; }
    public void setO3(String o3) { O3 = o3; }

    public String getO4() { return O4; }
    public void setO4(String o4) { O4 = o4; }

    public int getA() { return A; }
    public void setA(int a) { A = a; }

    public String getPICURL() { return PICURL; }
    public void setPICURL(String pIC) { PICURL = pIC; }

    public String getTYPE() { return TYPE; }
    public void setTYPE(String type) { TYPE = type; }

    public String getIP() { return IP; }
    public void setIP(String ip) { IP = ip; }
}
