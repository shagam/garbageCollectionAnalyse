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
import share.cli.Args;

/**
 *
 * @author eli
 */
public class GarbageCollectionAnalyse {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    
        int linesSkip = Args.getInteger("skipLines", args, "number of lines to skip before analyze");
        if (linesSkip == Integer.MAX_VALUE)
            linesSkip = 0;

        String file = Args.getString ("file", args, "java garbage logfile i.e.:   file=/var/js/java.log0    ");     
        Args.showAndVerify (true);
        if (file == null) {
            System.err.print("\n**** Err missing:   file=/var/js/java.log99  \n");
            System.exit(1);
        }
        
        try {
            InputStreamReader isr;
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));            

            String strLine = null;
            int lineCount = 0;
            long sum = 0;        
            int biggest = 0;
            int countNoMatch = 0;
            int firstSecond = 0;
            int lastSecond = 0;

    //2368.355: [GC (Allocation Failure)  409467K->288596K(414720K), 0.0143538 secs]
    //2368.369: [Full GC (Ergonomics)  288596K->208152K(414720K), 0.5288433 secs]

            String pattern = "(\\d+)\\.(\\d+):[ ]*.* (\\d+)\\.(\\d+) secs.*" ;
            Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

            int lines = 0;
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
                    long perSec_ = 0;
                    if (lastSecond > 0)
                        perSec_ =  sum / 10000 / lastSecond;
                    int a = 0;
                }
                int cnt = m.groupCount();
                if (cnt != 4) {
                    //groupCountWrong++;
                    continue;
                }
                lineCount ++;                
                String str = m.group(1);
                int timeSec = Integer.parseInt(str);
                str = m.group(2);
                int timeMili = Integer.parseInt(str);
                str = m.group(3);
                int delaySec = Integer.parseInt(str);
                str = m.group(4);
                int delayMili = Integer.parseInt(str);

                int time = timeSec * 1000 + timeMili;
                int delay = delaySec * 10000000 + delayMili;
                if (firstSecond == 0)
                    firstSecond = timeSec;
                lastSecond = timeSec;
                sum += delay;
                if (delay > biggest)
                    biggest = delay;

//                    if (nonBiggerLines < 10)
//                        System.out.print("\nvalRequired=" + valRequired + " lineVal=" + lineVal);                        
            }
            long delay = (lastSecond - firstSecond);
            long perSec =  sum / 10000;
            if (delay != 0)
                perSec /= delay;
            System.err.print("\nmiliPerSecPause=" + perSec);            

            System.err.print("\nlineCount=" + lineCount); 
//            System.err.print("\ngroupCountWrong=" + groupCountWrong);             
            System.err.print("\ncountNoMatch=" + countNoMatch);

            System.err.print("\n"); 
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}
