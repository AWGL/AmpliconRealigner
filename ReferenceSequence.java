package nhs.genetics.cardiff.framework;

import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import nhs.genetics.cardiff.framework.GenomicLocation;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper around htsjdk fasta functions. Extracts target from an indexed file.
 *
 * @author  Matt Lyon
 * @version 1.0
 * @since   2015-04-21
 */
public class ReferenceSequence {

    private static final Logger log = Logger.getLogger(ReferenceSequence.class.getName());

    private String referenceSequence;
    private GenomicLocation location;
    private File fastaFilePath, indexFilePath;
    private int padding = 0;

    public ReferenceSequence(GenomicLocation location, File fastaFilePath, File indexFilePath){
        this.location = location;
        this.fastaFilePath = fastaFilePath;
        this.indexFilePath = indexFilePath;
    }
    public ReferenceSequence(GenomicLocation location, File fastaFilePath, File indexFilePath, int padding){
        this.location = location;
        this.fastaFilePath = fastaFilePath;
        this.indexFilePath = indexFilePath;
        this.padding = padding;
    }

    public void populateReferenceSequence(){ //1-based

        //read fasta index
        FastaSequenceIndex refGenomeIndex = new FastaSequenceIndex(indexFilePath);

        //get fasta sequence
        try(IndexedFastaSequenceFile refGenomeFasta = new IndexedFastaSequenceFile(fastaFilePath, refGenomeIndex)) {

            //get sequence
            byte[] bytes = refGenomeFasta.getSubsequenceAt(location.getContig(), location.getStartPosition() - padding, location.getEndPosition() + padding).getBases();
            referenceSequence = new String(bytes, "UTF-8");

            refGenomeFasta.close();
        } catch (UnsupportedEncodingException e){
            log.log(Level.SEVERE, "Problem converting nucleotide sequence: " + e.toString());
        } catch(IOException e){
            log.log(Level.SEVERE, "Problem reading reference genome: " + e.toString());
        }

    }

    public boolean isRefAllNSites(){

        for (char base : referenceSequence.toCharArray()){
            if (base != 'N'){
                return false;
            }
        }

        return true;
    }

    public String getReferenceSequence() {
        return referenceSequence;
    }
    public int getLength(){
        return referenceSequence.length();
    }
}
