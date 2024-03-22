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

import org.apache.ctakes.assertion.eval.AssertionEvaluation;
import org.apache.ctakes.assertion.util.AssertionConst;

import java.util.ArrayList;

public class CrossValidateChi2GridSearch {

	public static void main(String[] args) throws Exception {
		
		AssertionEvaluation.useEvaluationLogFile = true;
		
		float[] threshs = {1f, 5f, 10f, 50f, 100f};
		for (Float chi2threshold : threshs ) {
			System.out.println("BEGIN Chi2 Grid Search with threshold = "+ Float.toString(chi2threshold));
//			AssertionEvaluation.evaluationLogFileOut.write("BEGIN Chi2 Grid Search with threshold = "+ Float.toString(chi2threshold)+"\n");
//			AssertionEvaluation.evaluationLogFileOut.flush();
			
			for (String attribute : AssertionConst.annotationTypes) {

				ArrayList<String> params = new ArrayList<String>();

				params.add("--train-dir"); 			params.add(AssertionConst.trainingDirectories.get(attribute));
				params.add("--models-dir"); 		params.add(AssertionConst.modelDirectory+Float.toString(chi2threshold));
				params.add("--cross-validation"); 	params.add("5");
				params.add("--feature-selection");	params.add(Float.toString(chi2threshold));

				// Build up an "ignore" string
				for (String ignoreAttribute : AssertionConst.annotationTypes) {
					if (!ignoreAttribute.equals(attribute)) { 

						if (ignoreAttribute.equals("historyOf")) {
							ignoreAttribute = ignoreAttribute.substring(0, ignoreAttribute.length()-2);
						}

						params.add("--ignore-" + ignoreAttribute);
					}
				}
				String[] paramList = params.toArray(new String[]{});

				//			System.out.println(Arrays.asList(paramList).toString());

				// Run the actual assertion training on just one attribute
				AssertionEvaluation.main( paramList );
			}
		}
		
		
		
	}
}
