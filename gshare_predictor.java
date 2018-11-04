/*
 * The MIT License
 *
 * Copyright 2018 Priyank Kashyap.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 *
 * @author Priyank Kashyap
 */
import java.math.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
public class gshare_predictor {
    private long mSize;
    private long mIndexBit;
    private long mGlobalLength;
    private double total_number_of_predicitions;
    private double number_of_mispredicts;
    private Map<String, Integer> mPredictor;
    private String globalHistoryReg;
    public gshare_predictor(long index, long global_history_length){
        mIndexBit=index;
        mGlobalLength=global_history_length;
        globalHistoryReg="";
        for (long j=0; j<mGlobalLength; j++){                                   //initalize the global history reg
            globalHistoryReg+="0";
        }
        mSize= (long) Math.pow(2, (double) index);                              //initialize the prediction table
        mPredictor=new HashMap();
        int initialValuePredictionTable=2;
        for(long i = 0; i <mSize; ++i){
            String binAddr= Long.toBinaryString(i);
            String prepend="";
            if(binAddr.length()<index){
                prepend= "0";
                for(int x=1; x<index-binAddr.length();x++){
                prepend+="0";
                }
                binAddr=prepend+binAddr;
            }        
            mPredictor.put(binAddr,initialValuePredictionTable);
        }
        total_number_of_predicitions=0;
        number_of_mispredicts=0;
    }
    
    public void predict(String address, char outcome){
        String reqIndex = address.substring(address.length()-(int)mIndexBit-2,address.length()-2);
        String toXOR    = reqIndex.substring(0,(int) mGlobalLength);
        String remainder= reqIndex.substring((int)mGlobalLength);
        int global_hist_XOR = Integer.parseInt(globalHistoryReg, 2);
        int indexXOR        = Integer.parseInt(toXOR, 2);
        int xor_result= indexXOR^global_hist_XOR;
        String XOR_result_string = Integer.toBinaryString(xor_result);
        
        String prediction_table_index=XOR_result_string+remainder;
        String prepend="";
        if(prediction_table_index.length()<mIndexBit){
            prepend= "0";
            for(int x=1; x<mIndexBit-prediction_table_index.length();x++){
                prepend+="0";
            }
            prediction_table_index=prepend+prediction_table_index;
        }        
        //System.out.print("full index: "+reqIndex +" toXOR: " + toXOR +" remainder addr: " +remainder +" final address: "+ prediction_table_index+ " ");
        int predicited_value = (int) mPredictor.get(prediction_table_index);
        total_number_of_predicitions++;
        update_branch_prediciton(prediction_table_index, predicited_value, outcome);
    }
    
    public void update_branch_prediciton(String addr, int prediciton, char outcome){
        //double predicited_value= (double) mPredictor.get(addr);
        int temp=prediciton;
        if(prediciton==2 || prediciton==3){ //prediction is taken but it was not taken --> mispredict
            if(outcome=='n'){
                temp--;
                mPredictor.put(addr, temp);
                number_of_mispredicts++;
            }
            else{
                if(prediciton==2){
                    temp++;
                    mPredictor.put(addr, temp);
                }
            }
        }
        else if(prediciton==0 || prediciton==1){ //prediction is not taken but it was taken --> mispredict
            if(outcome=='t'){
                temp++;
                mPredictor.put(addr, temp);
                number_of_mispredicts++;
            }
            else{
                if(prediciton == 1){
                    temp--;
                    mPredictor.put(addr, temp);
                }
            }
        }
        int int_addr= Integer.parseInt(addr, 2);
        //System.out.println("global hist: "+ globalHistoryReg+ " "+(int)total_number_of_predicitions+" reqIndex is " + int_addr +" Prediciton was: "+ prediciton + " Updated prediciton: "+temp);
        update_global_history(outcome);

    }
    void update_global_history(char outcome){
        String temp="";
        if(outcome=='t'){
            temp= "1" + globalHistoryReg.substring(0, (int) mGlobalLength-1) ;
        }
        else{
            temp= "0" + globalHistoryReg.substring(0, (int) mGlobalLength-1);
        }
        globalHistoryReg=temp;
    }
    public void get_stats(){
        System.out.println("OUTPUT");
        System.out.println(" number of predictions: "+ (int) total_number_of_predicitions);
        System.out.println(" number of mispredictions: "+ (int) number_of_mispredicts);
        String t=String.format("%.02f",((number_of_mispredicts/total_number_of_predicitions) *100));
        System.out.println(" misprediction rate: "+ t + "%");
        System.out.println("FINAL GSHARE CONTENTS");
        for(long i = 0; i <mSize; ++i){
            String binAddr= Long.toBinaryString(i);
            String prepend="";
            if(binAddr.length()<mIndexBit){
                prepend= "0";
                for(int x=1; x<mIndexBit-binAddr.length();x++){
                prepend+="0";
                }
                binAddr=prepend+binAddr;
            }        
            System.out.println(i+ " "+mPredictor.get(binAddr));
        }
        System.out.println();
    }
}
