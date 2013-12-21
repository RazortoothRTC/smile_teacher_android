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
package org.smilec.smile.bu.json;

import org.json.JSONObject;
import org.smilec.smile.bu.Constants;
import org.smilec.smile.domain.Student;

public class StudentJSONParser {

    public static final Student process(JSONObject object) {

        String ip = object.optString(Constants.IP);
        String name = object.optString(Constants.NAME);

        Student student = new Student(ip, name);
        return student;
    }
}
