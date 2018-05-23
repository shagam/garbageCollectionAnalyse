/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package garbagecollectionanalyse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author eli
 */
public class GarbageCollectionAnalyse {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
            
        String file = Args.getString ("file", args, "java garbage logfile i.e.:   file=/var/js/java.log0    ");
        int threshold = Args.getInteger("thresholdMili", args, "number of lines above threshold");
        float thresholdFloat = threshold;
        thresholdFloat /= 1000;
        int aboveThresholdCount = 0;
        
        boolean biggest = Args.getBool("biggest", args, "find biggest freeze"); 

        int linesSkip = Args.getInteger("skipLines", args, "number of lines to skip before analyze");
        if (linesSkip == Integer.MAX_VALUE)
            linesSkip = 0;
        boolean pause = Args.getBool("pause", args, "measure pause mili per sec; default measure gc time"); 
               
        boolean debug = Args.getBool("debug", args, "print debug info"); 
        boolean quiet = Args.getBool("quiet", args, "avoid print of param types");
        
        Args.showAndVerify (! quiet);
        if (file == null) {
            System.err.print("\n**** Err missing:   file=/var/js/java.log99  \n");
            System.exit(1);
        }
        
        try {
            InputStreamReader isr;
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));            

            String strLine = null;
            int anlyzedLineCount = 0;
            float totalFreezeTime = 0;        
            float biggestFreeze = 0;
            int countNoMatch = 0;
            float firstTimeStampSecond = 0;
            float lastTimeStampSecond = 0;

            String regex;
            if (pause) // use different lines for stopWorld duration
                //2368.355: [GC (Allocation Failure)  409467K->288596K(414720K), 0.0143538 secs]
                //2368.369: [Full GC (Ergonomics)  288596K->208152K(414720K), 0.5288433 secs]
                regex = "([\\.\\d]+): Total time for which application threads were stopped: ([\\.\\d]+) seconds" ;
            else
                //0.277: Total time for which application threads were stopped: 0.0279608 seconds
                regex = "([\\.\\d]+):[ ]*.* ([\\.\\d]+) secs.*" ;

            Pattern pat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

            int lines = 0;
            int lineOfBiggestDelay = 0;
            String lineOfBiggestDelayStr = null;
            
            while ((strLine = reader.readLine()) != null) {
                lines ++;
                if (lines < linesSkip)
                    continue;
                
                Matcher m = pat.matcher(strLine);
                boolean matchFound = m.find();
                if (! matchFound) {
                    countNoMatch++;
                    //assert countNoMatch < 2;
                    continue;
                }
                if (strLine.contains("Full")) {
                    float perSec_ = 0;
                    if (lastTimeStampSecond > 0)
                        perSec_ =  totalFreezeTime / 1000 / lastTimeStampSecond;
                    int a = 0;
                }
                int cnt = m.groupCount();
                if (cnt != 2) {
                    //groupCountWrong++;
                    continue;
                }
                    anlyzedLineCount ++;                
                String str = m.group(1);
                float timeStampSec = Float.parseFloat(str);
                str = m.group(2);
                float freezeDelay = Float.parseFloat(str);
                
                // collect lines abouve threshold
                if (freezeDelay > thresholdFloat)
                    aboveThresholdCount ++;

                //int timeStampMili = timeStampSec * 1000 + timeMili;
                
                if (firstTimeStampSecond == 0)
                    firstTimeStampSecond = timeStampSec;
                lastTimeStampSecond = timeStampSec;
                totalFreezeTime += freezeDelay;
                if (freezeDelay > biggestFreeze) {
                    biggestFreeze = freezeDelay;
                    lineOfBiggestDelayStr = strLine;
                    lineOfBiggestDelay = lines;

//                    if (biggestFreeze >= 4000000) {
//                        int a = 5;
//                    }
                }
                       
            }
            float runningDuration = (lastTimeStampSecond - firstTimeStampSecond);
            float perSec =  totalFreezeTime;
            if (runningDuration != 0)
                perSec /= runningDuration;
            if (! quiet)
                System.err.print("\n");
            System.err.print("\nrunningDurationSec=" + runningDuration + "  ");
            secToHourMinuteSec ((int) runningDuration);
            System.err.print("   totalLines=" + lines + "   lineAnalyzedCount=" + anlyzedLineCount);
            
            System.err.print("\naverageFreezePerSec=" + perSec);
            
            System.err.print("\naverageSingleFreezeSec=" + (totalFreezeTime / anlyzedLineCount));
            if (debug)            
            System.err.print("\ncountFreezePerSec=" + (anlyzedLineCount / runningDuration));            
            
            if (debug) {

//            System.err.print("\ngroupCountWrong=" + groupCountWrong);             
                System.err.print("\ncountNoMatch=" + countNoMatch);
            }
            
            if (biggest)
                System.err.print("\nbeggestFreeze=" + biggestFreeze + "  lineNum=" + lineOfBiggestDelay);
            // + "\n" + lineOfBiggestDelayStr);
            if (threshold != Integer.MAX_VALUE)
                System.err.print("\naboveThresholdCount=" + aboveThresholdCount + "     thresholdMili=" + threshold);

            System.err.print("\n"); 
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    
    static void secToHourMinuteSec (int milliseconds) {
        int seconds = (int) (milliseconds) % 60 ;
        int minutes = (int) ((milliseconds / (60)) % 60);
        int hours   = (int) ((milliseconds / (60*60)) /*% 24*/);
        System.err.printf ("%d:%02d:%02d", hours, minutes, seconds);
    }
}
