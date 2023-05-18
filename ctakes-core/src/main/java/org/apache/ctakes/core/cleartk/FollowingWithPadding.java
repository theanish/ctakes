package org.apache.ctakes.core.cleartk;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Bounds;
import org.cleartk.ml.feature.extractor.CleartkExtractor.Following;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FollowingWithPadding extends Following {

  public int dims;
  
  public FollowingWithPadding(int end, int dims) {
    super(end);
    this.dims = dims;
  }
  
  @Override
  public <SEARCH_T extends Annotation> List<Feature> extract(JCas jCas,
      Annotation focusAnnotation, Bounds bounds,
      Class<SEARCH_T> annotationClass, FeatureExtractor1<SEARCH_T> extractor)
      throws CleartkExtractorException {
    LinkedList<Feature> rawFeats = new LinkedList<>(super.extract(jCas, focusAnnotation, bounds, annotationClass, extractor));
    List<Feature> processedFeats = new ArrayList<>();

    for(Feature feat : rawFeats){
      if(feat.getValue().toString().startsWith("OOB")){
        // add one feature for each dimension and set it to 0.
        for(int j = 0; j < this.dims; j++){
          processedFeats.add(new Feature(feat.getName() + "_" + j, 0.0));
        }
      }else{
        processedFeats.add(feat);
      }
    }
    return processedFeats;
  }

  /*
  @Override
  protected <T extends Annotation> List<T> select(JCas jCas,
      Annotation focusAnnotation, Class<T> annotationClass, int count) {
    List<T> validList = super.select(jCas, focusAnnotation, annotationClass, count);
    
    // Pad the end of the list with repeats of the last element
    while(validList.size() < count){
      validList.add(validList.get(validList.size()-1));
    }
    
    return validList;
  }
  */
}
