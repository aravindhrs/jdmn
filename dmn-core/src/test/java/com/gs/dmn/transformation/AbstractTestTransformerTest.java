/*
 * Copyright 2016 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.gs.dmn.transformation;

import com.gs.dmn.log.BuildLogger;
import com.gs.dmn.runtime.Pair;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public abstract class AbstractTestTransformerTest extends AbstractTransformerTest {
    protected void doTest(String inputTestFilePath, String inputModelFilePath, String expectedOutputPath, Pair<String, String>... extraInputParameters) throws Exception {
        File outputFolder = new File("target/" + expectedOutputPath);
        outputFolder.mkdirs();

        Path inputPath = new File(inputTestFilePath).toPath();
        Path inputModelPath = new File(inputModelFilePath).toPath();
        Map<String, String> inputParameters = makeInputParameters();
        for (Pair<String, String> pair: extraInputParameters) {
            inputParameters.put(pair.getLeft(), pair.getRight());
        }
        FileTransformer transformer = makeTransformer(inputModelPath, inputParameters, LOGGER);
        transformer.transform(inputPath, outputFolder.toPath());

        File expectedOutputFolder = new File(resource(expectedOutputPath));
        compareFile(expectedOutputFolder, outputFolder);
    }

    protected abstract FileTransformer makeTransformer(Path inputModelPath, Map<String, String> inputParameters, BuildLogger logger);
}
