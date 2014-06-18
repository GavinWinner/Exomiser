/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.charite.compbio.exomiser.writer;

import de.charite.compbio.exomiser.common.SampleData;
import de.charite.compbio.exomiser.exome.Gene;
import de.charite.compbio.exomiser.filter.Filter;
import de.charite.compbio.exomiser.priority.Priority;
import de.charite.compbio.exomiser.util.ExomiserSettings;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class HtmlResultsWriterTest {
    
    HtmlResultsWriter instance;

    public HtmlResultsWriterTest() {
        instance = new HtmlResultsWriter();
    }

    @Test
    public void testWrite() {
        SampleData sampleData = new SampleData();
        sampleData.setGeneList(new ArrayList<Gene>());
        ExomiserSettings settings = new ExomiserSettings.Builder().outFileName("testWrite.html").build();
        List<Filter> filterList = null;
        List<Priority> priorityList = null;
        //TODO: make this work!
//        instance.write(sampleData, settings, filterList, priorityList);
//        assertTrue(Paths.get("testWrite.html").toFile().exists());
//        assertTrue(Paths.get("testWrite.html").toFile().delete());
    }
    
}
