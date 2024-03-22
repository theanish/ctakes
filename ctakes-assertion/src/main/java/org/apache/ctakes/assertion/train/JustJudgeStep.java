/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ctakes.assertion.train;

import org.apache.ctakes.assertion.pipelines.RunJudgeAttributeInstances;
import org.apache.ctakes.assertion.util.AssertionConst;

import java.util.ArrayList;

public class JustJudgeStep {

	public static void main(String[] args) throws Exception {
	
//		ReadAndPreprocessForAttributeModels.main(null);
//		
//		TrainAttributeModels.main(null);
//		
//		TestAttributeModels.main(null);



		// Set up parameters for judging test
		// output of test step becomes input for runjudge
		ArrayList<String> params;
		params = new ArrayList<String>();
		
		//@Option(
		//		name = "--input-dir",
		//		usage = "where to read the fully-annotated xmi data from",
		//		required = true)
		//		public String inputDir = AssertionConst.evalOutputDir;
		params.add("--input-dir");
		params.add(AssertionConst.evalOutputDir);

		//@Option(
		//		name = "--output-dir",
		//		usage = "where to write the 'adjudicated' instances in xmi format to",
		//		required = true)
		//		public String outputDir = AssertionConst.instanceGatheringOutputDir;
		params.add("--output-dir");
		params.add(AssertionConst.instanceGatheringOutputDir);
		
		String [] parmsAsArray;
		parmsAsArray = params.toArray(new String[] {});
		
		RunJudgeAttributeInstances.main(parmsAsArray);

		// Run the actual assertion preprocessing on just one data source
		// if (true) /** TODO Should this be done here? **/ AssertionEvaluation.main(parmsAsArray);

	}
}
