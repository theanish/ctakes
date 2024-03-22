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
package org.apache.ctakes.assertion.attributes.features;

import org.apache.ctakes.assertion.attributes.history.HistoryAttributeClassifier;
import org.apache.ctakes.dependency.parser.util.DependencyUtility;
import org.apache.ctakes.typesystem.type.syntax.ConllDependencyNode;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.uima.jcas.JCas;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

import java.util.*;


/** HistoryFeaturesExtractor
 * 		Ports the features and classification decisions of the first version (logic) of the subject tool
 * 
 * @author shalgrim
 *
 */
public class HistoryFeaturesExtractor implements FeatureExtractor1<IdentifiedAnnotation> {
	
	
	@Override
	public List<Feature> extract(JCas jCas, IdentifiedAnnotation arg) {
		
		List<Feature> features = new ArrayList<>();
		
		// Pull in general dependency-based features -- externalize to another extractor?
	    ConllDependencyNode node = DependencyUtility.getNominalHeadNode(jCas, arg);
	    if (node!= null) {
//	      features.add(new Feature("DEPENDENCY_HEAD", node));
	    	features.add(new Feature("DEPENDENCY_HEAD_word", node.getCoveredText()));
//	    	features.add(new Feature("DEPENDENCY_HEAD_pos", node.getPostag()));
	    	features.add(new Feature("DEPENDENCY_HEAD_deprel", node.getDeprel()));
//	    	features.add(new Feature("DEPENDENCY_HEAD_lemma", node.getLemma()));
		}
	    
	    HashMap<String, Boolean> featsMap = HistoryAttributeClassifier.extract(jCas, arg);

	    // Pull in all the features that were used for the rule-based module
	    features.addAll( hashToFeatureList(featsMap) );
	    
	    // Pull in the result of the rule-based module as well
	    features.add(new Feature("HISTORY_CLASSIFIER_LOGIC", HistoryAttributeClassifier.classifyWithLogic(featsMap)));
	    
	    // Add whether it is token preceded by "h/o"
	    //features.add(new Feature("PRECEDED_BY_H_SLASH_O", HistoryAttributeClassifier.precededByH_O(jCas, arg)));
	    
	    return features;
	}

	private static Collection<? extends Feature> hashToFeatureList(
			HashMap<String, Boolean> featsIn) {
		
		Collection<Feature> featsOut = new HashSet<>();
		for (String featName : featsIn.keySet()) {
			featsOut.add(new Feature(featName,featsIn.get(featName)));
		}
		
		return featsOut;
	}

}
