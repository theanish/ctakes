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
package org.apache.ctakes.temporal.ae.feature.duration;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.ctakes.temporal.duration.Utils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DurationExpectationFeatureExtractor implements FeatureExtractor1 {

  @Override
  public List<Feature> extract(JCas view, Annotation annotation) throws CleartkExtractorException { 

    List<Feature> features = new ArrayList<>();
    File durationLookup = new File(Utils.durationDistributionPath);
    
    String eventText;
    try {
      eventText = Utils.normalizeEventText(view, annotation);
    } catch (AnalysisEngineProcessException e1) {
      e1.printStackTrace();
      return features;
    } 
    
    Map<String, Map<String, Float>> textToDistribution = null;
    try {
      textToDistribution = Files.readLines(durationLookup, Charsets.UTF_8, new Utils.Callback());
    } catch(IOException e) {
      e.printStackTrace();
      return features;
    }
    
    Map<String, Float> eventDistribution = textToDistribution.get(eventText);
    if(eventDistribution == null) {
      features.add(new Feature("no_duration_info"));
      return features;
    }
    
    float expectation = Utils.expectedDuration(eventDistribution);
    features.add(new Feature("expected_duration", expectation));
    
    for(String bin : Utils.bins) {
      features.add(new Feature(bin, eventDistribution.get(bin)));
    }

    String largestBin = null;
    float largestValue = 0f;
    for(String bin : Utils.bins) {
      if(eventDistribution.get(bin) > largestValue) {
        largestBin = bin;
        largestValue = eventDistribution.get(bin);
      }
    }
    features.add(new Feature("largest_bin_" + largestBin));
    
    return features;
  }
}