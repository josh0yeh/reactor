/*
 * Copyright (c) 2015 - present Nebula Bay.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tascape.reactor.task;

import com.tascape.reactor.data.AbstractCaseData;

/**
 *
 * @author linsong wang
 */
public class SampleData extends AbstractCaseData {

    public String caseParameter = "";

    private static final SampleData[] DATA = new SampleData[]{
        new SampleData() {
            {
                caseParameter = "scenario one";
            }

            @Override
            public String getValue() {
                return "scenario one";
            }
        },
        new SampleData() {
            {
                caseParameter = "scenario two";
            }

            @Override
            public String getValue() {
                return "scenario two";
            }
        },
        new SampleData() {
            {
                caseParameter = "scenario three";
                setValue(caseParameter);
            }

            @Override
            public String getValue() {
                return "scenario three";
            }
        }};

    public SampleData[] getData() {
        return DATA;
    }
}
