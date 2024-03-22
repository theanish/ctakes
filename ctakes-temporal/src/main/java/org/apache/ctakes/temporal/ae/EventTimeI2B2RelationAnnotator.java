/**
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
package org.apache.ctakes.temporal.ae;

import com.google.common.collect.Lists;
import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.relationextractor.ae.RelationExtractorAnnotator;
import org.apache.ctakes.relationextractor.ae.features.PartOfSpeechFeaturesExtractor;
import org.apache.ctakes.relationextractor.ae.features.RelationFeaturesExtractor;
import org.apache.ctakes.temporal.ae.feature.*;
import org.apache.ctakes.typesystem.type.relation.BinaryTextRelation;
import org.apache.ctakes.typesystem.type.relation.RelationArgument;
import org.apache.ctakes.typesystem.type.relation.TemporalTextRelation;
import org.apache.ctakes.typesystem.type.textsem.EventMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.TimeMention;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.CleartkAnnotator;
import org.cleartk.ml.DataWriter;
import org.cleartk.ml.jar.DefaultDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import org.cleartk.ml.jar.GenericJarClassifierFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@PipeBitInfo(
		name = "E-T I2B2 TLinker",
		description = "Creates Event - Time TLinks with I2B2 model.",
		dependencies = { PipeBitInfo.TypeProduct.SECTION, PipeBitInfo.TypeProduct.SENTENCE,
				PipeBitInfo.TypeProduct.EVENT, PipeBitInfo.TypeProduct.TIMEX },
		products = { PipeBitInfo.TypeProduct.TEMPORAL_RELATION }
)
public class EventTimeI2B2RelationAnnotator extends RelationExtractorAnnotator {

	public static AnalysisEngineDescription createDataWriterDescription(
			Class<? extends DataWriter<String>> dataWriterClass,
					File outputDirectory,
					double probabilityOfKeepingANegativeExample) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(
				EventTimeI2B2RelationAnnotator.class,
				CleartkAnnotator.PARAM_IS_TRAINING,
				true,
				DefaultDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
				dataWriterClass,
				DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
				outputDirectory,
				RelationExtractorAnnotator.PARAM_PROBABILITY_OF_KEEPING_A_NEGATIVE_EXAMPLE,
				// not sure why this has to be cast; something funny going on in uimaFIT maybe?
				(float) probabilityOfKeepingANegativeExample);
	}

	public static AnalysisEngineDescription createEngineDescription(String modelPath)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(
				EventTimeI2B2RelationAnnotator.class,
				CleartkAnnotator.PARAM_IS_TRAINING,
				false,
				GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
				modelPath);
	}
	/**
	 * @deprecated use String path instead of File.
	 * ClearTK will automatically Resolve the String to an InputStream.
	 * This will allow resources to be read within from a jar as well as File.  
	 */	 
	@Deprecated
	public static AnalysisEngineDescription createEngineDescription(File modelDirectory)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(
				EventTimeI2B2RelationAnnotator.class,
				CleartkAnnotator.PARAM_IS_TRAINING,
				false,
				GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
				new File(modelDirectory, "model.jar"));
	}

	@Override
	@SuppressWarnings( "unchecked" )
	protected List<RelationFeaturesExtractor<IdentifiedAnnotation,IdentifiedAnnotation>> getFeatureExtractors()
			throws ResourceInitializationException {
		return Lists.newArrayList(
//				new TokenFeaturesExtractor()
				new UnexpandedTokenFeaturesExtractor() //use unexpanded version for i2b2 data
				, new PartOfSpeechFeaturesExtractor()
				, new TemporalAttributeFeatureExtractor()
				//				, new EventTimeFlatTreeFeatureExtractor()
				//				, new TemporalPETExtractor()
				//, new TemporalPathExtractor()
				//				, new EventVerbRelationTreeExtractor()
				, new NumberOfEventTimeBetweenCandidatesExtractor()
				//				, new SectionHeaderRelationExtractor()
				, new NearbyVerbTenseRelationExtractor()
				, new CheckSpecialWordRelationExtractor()
				, new NearestFlagFeatureExtractor()
				, new DependencyPathFeaturesExtractor()
				, new DependencyFeatureExtractor()
				//				, new SRLRelationFeaturesExtractor()// tried, but not helpful
				, new EventArgumentPropertyExtractor()
				, new OverlappedHeadFeaturesExtractor()
				//				, new EventTimeRelationFeatureExtractor()
				, new ConjunctionRelationFeaturesExtractor()
				//				, new EventPositionRelationFeaturesExtractor() //tried, but not helpful
				, new TimeXRelationFeaturesExtractor()
//				, new TemporalPETFlatExtractor()
//				, new TimeXPropertyRelationFeaturesExtractor()
				//				, new TimeWordTypeRelationExtractor() //tried, but not helpful
				//				, new EventIndexOfSameSentenceRelationFeaturesExtractor() //tried, but not helpful
				);
	}

	@Override
	protected Class<? extends Annotation> getCoveringClass() {
		return Sentence.class;
	}

	@Override
	public List<IdentifiedAnnotationPair> getCandidateRelationArgumentPairs(
			JCas jCas,
			Annotation sentence) {
		Map<EventMention, Collection<EventMention>> coveringMap =
				JCasUtil.indexCovering(jCas, EventMention.class, EventMention.class);

		List<IdentifiedAnnotationPair> pairs = Lists.newArrayList();
		for (EventMention event : JCasUtil.selectCovered(jCas, EventMention.class, sentence)) {
			boolean eventValid = false;
			if (event.getClass().equals(EventMention.class)) {//event is a gold event
				eventValid = true;
			}

			if(eventValid){
				// ignore subclasses like Procedure and Disease/Disorder
				if(this.isTraining()){//if training mode, train on both gold event and span-overlapping system events
					for (TimeMention time : JCasUtil.selectCovered(jCas, TimeMention.class, sentence)) {
//						Collection<EventMention> eventList = coveringMap.get(event);
//						for(EventMention covEvent : eventList){
//							pairs.add(new IdentifiedAnnotationPair(covEvent, time));
//						}
						pairs.add(new IdentifiedAnnotationPair(event, time));
//						for(EventMention covedEvent : JCasUtil.selectCovered(jCas, EventMention.class, event)){//select covered events
//							pairs.add(new IdentifiedAnnotationPair(covedEvent, time));
//						}
					}
				}else{//if testing mode, only test on system generated events
					for (TimeMention time : JCasUtil.selectCovered(jCas, TimeMention.class, sentence)) {
						pairs.add(new IdentifiedAnnotationPair(event, time));
					}
				}
			}
		}

		return pairs;
	}

	@Override
	protected void createRelation(JCas jCas, IdentifiedAnnotation arg1,
			IdentifiedAnnotation arg2, String predictedCategory) {
		RelationArgument relArg1 = new RelationArgument(jCas);
		relArg1.setArgument(arg1);
		relArg1.setRole("Arg1");
		relArg1.addToIndexes();
		RelationArgument relArg2 = new RelationArgument(jCas);
		relArg2.setArgument(arg2);
		relArg2.setRole("Arg2");
		relArg2.addToIndexes();
		TemporalTextRelation relation = new TemporalTextRelation(jCas);
		relation.setArg1(relArg1);
		relation.setArg2(relArg2);
		relation.setCategory(predictedCategory);
		relation.addToIndexes();
	}


	@Override
	protected String getRelationCategory(
			Map<List<Annotation>, BinaryTextRelation> relationLookup,
			IdentifiedAnnotation arg1,
			IdentifiedAnnotation arg2) {
		BinaryTextRelation relation = relationLookup.get(Arrays.asList(arg1, arg2));
		String category = null;
		if (relation != null) {
			category = relation.getCategory();
		} else {
			relation = relationLookup.get(Arrays.asList(arg2, arg1));
			if (relation != null) {
				if(relation.getCategory().equals("OVERLAP")){
					category = relation.getCategory();
				}else if (relation.getCategory().equals("BEFORE")){
					category = "AFTER";
				}else if (relation.getCategory().equals("AFTER")){
					category = "BEFORE";
				}
			}
		}

		if(category!=null){
			if(!((EventMention)arg1).getClass().equals(EventMention.class)){
				System.out.println("find system-event relations: "+ arg1.getCoveredText() + " -"+category+"- " + arg2.getCoveredText());
			}else{
				System.out.println("find gold-event relations: "+ arg1.getCoveredText() + " -"+category+"- " + arg2.getCoveredText());
			}
		}

		if (category == null && coin.nextDouble() <= this.probabilityOfKeepingANegativeExample) {
			category = NO_RELATION_CATEGORY;
		}

		return category;
	}
}
