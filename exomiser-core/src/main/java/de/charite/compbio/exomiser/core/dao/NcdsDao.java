/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.charite.compbio.exomiser.core.dao;

import de.charite.compbio.exomiser.core.model.Variant;
import de.charite.compbio.exomiser.core.model.pathogenicity.NcdsScore;
import de.charite.compbio.exomiser.core.model.pathogenicity.PathogenicityData;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import htsjdk.tribble.readers.TabixReader;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
@Component
public class NcdsDao {

    private final Logger logger = LoggerFactory.getLogger(NcdsDao.class);

    private final TabixReader ncdsTabixReader;

    public NcdsDao(TabixReader ncdsTabixReader) {
        this.ncdsTabixReader = ncdsTabixReader;
    }

    @Cacheable(value = "mncds", key = "#variant.chromosomalVariant")
    public PathogenicityData getPathogenicityData(Variant variant) {
        // MNCDS has not been trained on missense variants so skip these
        if (variant.getVariantEffect() == VariantEffect.MISSENSE_VARIANT) {
            return new PathogenicityData();
        }
        return processResults(variant);
    }

    private PathogenicityData processResults(Variant variant) {
        String chromosome = variant.getChromosomeName();
        int start = variant.getPosition();
        int end = calculateEndPosition(variant);
        return getNcdsData(chromosome, start, end);
    }

    private int calculateEndPosition(Variant variant) {
        int end = variant.getPosition();
        //these end positions are calculated according to recommendation by Max and Peter who produced the NCDS score
        //don't change this unless they say. 
        if (isDeletion(variant)) {
            // test all deleted bases
            end += variant.getRef().length();
        } else if (isInsertion(variant)) {
            // test bases either side of insertion
            end += 1;
        }
        return end;
    }

    private static boolean isDeletion(Variant variant) {
        return variant.getAlt().equals("-");
    }

    private static boolean isInsertion(Variant variant) {
        return variant.getRef().equals("-");
    }
    
    private PathogenicityData getNcdsData(String chromosome, int start, int end) throws NumberFormatException {
        try {
            float ncds = Float.NaN;
            String line;
//            logger.info("Running tabix with " + chromosome + ":" + start + "-" + end);
            TabixReader.Iterator results = ncdsTabixReader.query(chromosome + ":" + start + "-" + end);
            while ((line = results.next()) != null) {
                String[] elements = line.split("\t");
                if (Float.isNaN(ncds)) {
                    ncds = Float.parseFloat(elements[2]);
                } else {
                    ncds = Math.max(ncds, Float.parseFloat(elements[2]));
                }
            }
            //logger.info("Final score " + ncds);
            if (!Float.isNaN(ncds)) {
                return new PathogenicityData(new NcdsScore(ncds));
            }
        } catch (IOException e) {
            logger.error("Unable to read from NCDS tabix file {}", ncdsTabixReader.getSource(), e);
        }
        return new PathogenicityData();
    }

}
