/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.charite.compbio.exomiser.core.model.pathogenicity;

import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class PathogenicityDataTest {
    
    private PathogenicityData instance;
    
    private static final float SIFT_PASS_SCORE = SiftScore.SIFT_THRESHOLD - 0.01f;
    private static final float SIFT_FAIL_SCORE = SiftScore.SIFT_THRESHOLD + 0.01f;

    private static final SiftScore SIFT_PASS = new SiftScore(SIFT_PASS_SCORE);
    private static final SiftScore SIFT_FAIL = new SiftScore(SIFT_FAIL_SCORE);

    private static final float POLYPHEN_PASS_SCORE = PolyPhenScore.POLYPHEN_THRESHOLD + 0.1f;
    private static final float POLYPHEN_FAIL_SCORE = PolyPhenScore.POLYPHEN_THRESHOLD - 0.1f;

    private static final PolyPhenScore POLYPHEN_PASS = new PolyPhenScore(POLYPHEN_PASS_SCORE);
    private static final PolyPhenScore POLYPHEN_FAIL = new PolyPhenScore(POLYPHEN_FAIL_SCORE);

    private static final float MTASTER_PASS_SCORE = MutationTasterScore.MTASTER_THRESHOLD + 0.01f;
    private static final float MTASTER_FAIL_SCORE = MutationTasterScore.MTASTER_THRESHOLD - 0.01f;

    private static final MutationTasterScore MTASTER_PASS = new MutationTasterScore(MTASTER_PASS_SCORE);
    private static final MutationTasterScore MTASTER_FAIL = new MutationTasterScore(MTASTER_FAIL_SCORE);

       
    @Test
    public void testHasPredictedScore_returnsFalseWhenNullsUsedInConstructor() {
        instance = new PathogenicityData(null, null);
        assertThat(instance.hasPredictedScore(), is(false));
    }
    
    @Test
    public void testGetPredictedPathogenicityScores_isEmptyWhenNullsUsedInConstructor() {
        instance = new PathogenicityData(null, null);
        assertThat(instance.getPredictedPathogenicityScores().isEmpty(), is(true));
    }
    
    @Test
    public void testPathogenicityData_RemovesNullsUsedInConstructor() {
        instance = new PathogenicityData(POLYPHEN_FAIL, null);
        assertThat(instance.getPredictedPathogenicityScores().isEmpty(), is(false));
        assertThat(instance.getPredictedPathogenicityScores().size(), equalTo(1));
    }
    
    @Test
    public void testGetMostPathogenicScore_ReturnsNullWhenNoScorePresent() {
        instance = new PathogenicityData();
        PathogenicityScore mostPathogenicScore = instance.getMostPathogenicScore();
        assertThat(mostPathogenicScore, nullValue());
    }
    
    @Test
    public void testGetMostPathogenicScore_ReturnsOnlyScoreWhenOneScorePresent() {
        instance = new PathogenicityData(POLYPHEN_FAIL);
        PathogenicityScore mostPathogenicScore = instance.getMostPathogenicScore();
        assertThat(mostPathogenicScore, equalTo((PathogenicityScore) POLYPHEN_FAIL));
    }
    
    @Test
    public void testGetMostPathogenicScore_ReturnsMostPathogenicScore() {
        instance = new PathogenicityData(POLYPHEN_FAIL, SIFT_PASS);
        PathogenicityScore mostPathogenicScore = instance.getMostPathogenicScore();
        assertThat(mostPathogenicScore, equalTo((PathogenicityScore) SIFT_PASS));
    }
    
    @Test
    public void testGetPolyPhenScore() {
        instance = new PathogenicityData(POLYPHEN_FAIL);
        PolyPhenScore result = instance.getPolyPhenScore();
        assertThat(result, equalTo(POLYPHEN_FAIL));
    }

    @Test
    public void testGetMutationTasterScore() {
        instance = new PathogenicityData(MTASTER_PASS);
        MutationTasterScore result = instance.getMutationTasterScore();
        assertThat(result, equalTo(MTASTER_PASS));
    }
    
        @Test
    public void testGetSiftScore() {
        instance = new PathogenicityData(SIFT_FAIL);
        SiftScore result = instance.getSiftScore();
        assertThat(result, equalTo(SIFT_FAIL));
    }

    @Test
    public void testGetCaddScore() {
        CaddScore caddScore = new CaddScore(POLYPHEN_PASS_SCORE);
        instance = new PathogenicityData(caddScore);
        CaddScore result = instance.getCaddScore();
        assertThat(result, equalTo(caddScore));
    }

    @Test
    public void testGetPredictedPathogenicityScores() {
        instance = new PathogenicityData(POLYPHEN_PASS, MTASTER_PASS, SIFT_FAIL);
        List<PathogenicityScore> expResult = new ArrayList<>();
        expResult.add(POLYPHEN_PASS);
        expResult.add(MTASTER_PASS);
        expResult.add(SIFT_FAIL);
        
        List<PathogenicityScore> result = instance.getPredictedPathogenicityScores();
        assertThat(result, equalTo(expResult));
    }

    @Test
    public void testHasPredictedScore() {
        instance = new PathogenicityData(POLYPHEN_PASS, MTASTER_PASS, SIFT_FAIL);
        boolean result = instance.hasPredictedScore();
        assertThat(result, is(true));
    }
    
    @Test
    public void testHasPredictedScoreForSource_isTrue() {
        instance = new PathogenicityData(POLYPHEN_PASS);
        boolean result = instance.hasPredictedScore(PathogenicitySource.POLYPHEN);
        assertThat(result, is(true));
    }
    
    @Test
    public void testHasPredictedScoreForSource_isFalse() {
        instance = new PathogenicityData();
        boolean result = instance.hasPredictedScore(PathogenicitySource.POLYPHEN);
        assertThat(result, is(false));
    }

    @Test
    public void testGetPredictedScore_scorePresent() {
        instance = new PathogenicityData(POLYPHEN_PASS);
        PathogenicityScore result =  instance.getPredictedScore(PathogenicitySource.POLYPHEN);
        assertThat(result.getScore(), equalTo(POLYPHEN_PASS.getScore()));
        assertThat(result.getSource(), equalTo(POLYPHEN_PASS.getSource()));
        assertThat((PolyPhenScore) result, equalTo(POLYPHEN_PASS));
    }
    
    @Test
    public void testGetPredictedScore_scoreMissingReturnsNull() {
        instance = new PathogenicityData();
        PathogenicityScore result = instance.getPredictedScore(PathogenicitySource.POLYPHEN);
        assertThat(result, is(nullValue()));
    }
    
    @Test
    public void testHasNoPredictedScore() {
        instance = new PathogenicityData();
        boolean result = instance.hasPredictedScore();
        assertThat(result, is(false));
    }

    @Test
    public void testHashCodeEquals() {
        instance = new PathogenicityData();
        PathogenicityData otherInstance = new PathogenicityData();
        assertThat(instance.hashCode(), equalTo(otherInstance.hashCode()));
    }
    
    @Test
    public void testHashCodeNotEquals() {
        instance = new PathogenicityData();
        PathogenicityData otherInstance = new PathogenicityData(MTASTER_FAIL);
        assertThat(instance.hashCode(), not(otherInstance.hashCode()));
    }

    @Test
    public void testNotEquals() {
        instance = new PathogenicityData();
        PathogenicityData otherInstance = new PathogenicityData(MTASTER_FAIL);
        assertThat(instance.equals(otherInstance), is(false));
    }
    
    @Test
    public void testEquals() {
        instance = new PathogenicityData(MTASTER_FAIL);
        PathogenicityData otherInstance = new PathogenicityData(MTASTER_FAIL);
        assertThat(instance.equals(otherInstance), is(true));
    }

    @Test
    public void testToString() {
        instance = new PathogenicityData(MTASTER_FAIL, POLYPHEN_PASS, SIFT_PASS);
        System.out.println(instance);
    }
}
