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

import com.tascape.reactor.driver.JacocoClient;
import com.tascape.reactor.driver.CaseDriver;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic Jacoco exec data collection case.
 *
 * @author linsong wang
 */
public class JacocoCase extends AbstractCase {
    private static final Logger LOG = LoggerFactory.getLogger(JacocoCase.class);

    public static final CaseDriver JACOCO = new CaseDriver(JacocoCase.class, JacocoClient.class);

    private final JacocoClient jacocoClient;

    public JacocoCase() {
        this.jacocoClient = super.getEntityDriver(JACOCO);
    }

    @Before
    public void setUp() throws Exception {
        LOG.debug("Run something before case");
        LOG.debug("Please override");
    }

    @After
    public void tearDown() throws Exception {
        LOG.debug("Run something after case");
        LOG.debug("Please override");
    }

    @Override
    public String getApplicationUnderTask() {
        LOG.debug("Please override");
        return "reactor";
    }
}
