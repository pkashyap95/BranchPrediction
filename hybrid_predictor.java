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
public class hybrid_predictor {
    private gshare_predictor mGshare;
    private bimodal_predictor mBimodal;
    private double total_number_of_predicitions;
    private double number_of_mispredicts;
    private long mSize;
    private long mIndexBit;
    private Map<String, Integer> mChooseTable;
    public hybrid_predictor(long k, long index_gshare, long gshare_global_history, long bimodal_index){
        mGshare=new gshare_predictor(index_gshare, gshare_global_history);
        mBimodal=new bimodal_predictor(bimodal_index);
        total_number_of_predicitions=0;
        number_of_mispredicts=0;
        mSize= (long) Math.pow(2, k);
        mIndexBit=k;
        mChooseTable=new HashMap();
        int initialValuePredictionTable=1;
        int indexBits= ((int)mIndexBit+1)-1;
        for(long i = 0; i <mSize; ++i){
            String binAddr= Long.toBinaryString(i);
            String prepend="";
            if(binAddr.length()<indexBits){
                prepend= "0";
                for(int x=1; x<indexBits-binAddr.length();x++){
                prepend+="0";
                }
                binAddr=prepend+binAddr;
            }        
            mChooseTable.put(binAddr,initialValuePredictionTable);
        }
    }
    
    public void predict(String address, char outcome){
        //int t= Integer.parseInt(address, 2);
        //System.out.println("=" + (int) total_number_of_predicitions + " " + t + " "+ outcome);
        int predict_val_hybrid_g=mGshare.hybrid_predict(address, outcome);
        int predict_val_hybrid_b=mBimodal.hybrid_predict(address, outcome);
        String reqIndex = address.substring((address.length()-1)-((int)mIndexBit+1),address.length()-2);
        int predicited_value = (int) mChooseTable.get(reqIndex);
        int add= Integer.parseInt(reqIndex, 2);
        //System.out.println("    CP:" + add + " " + predicited_value);
        if(predicited_value >=2){
            //update gshare counter
//            if(predict_val_hybrid_g >=2 && outcome=='n'){
//                number_of_mispredicts++;
//            }
//            else if(predict_val_hybrid_g <=1 && outcome=='t'){
//                number_of_mispredicts++;
//            }
            mGshare.hybrid_update(address, outcome, predict_val_hybrid_g);
            mGshare.update_global_history(outcome);
        }
        else if(predicited_value <=1){
            //update bimodal counter
//            if(predict_val_hybrid_b >=2 && outcome=='n'){
//                number_of_mispredicts++;
//            }
//            else if(predict_val_hybrid_b <=1 && outcome=='t'){
//                number_of_mispredicts++;
//            }
            mBimodal.hybrid_update(address, outcome, predict_val_hybrid_b);
            //update gshare global history
            mGshare.update_global_history(outcome);
        }
        
        
        total_number_of_predicitions++;
        update_values(predict_val_hybrid_g, predict_val_hybrid_b, predicited_value, reqIndex, outcome);
    }
    
    public void update_values(int hybrid_predicition_gshare, int hybrid_predicition_bimodal,int chooser_table_val, String address, char outcome){
        int temp=chooser_table_val;
        //int add= Integer.parseInt(address, 2);
        
        //both incorrect or both correct
        if( (hybrid_predicition_gshare >= 2 && hybrid_predicition_bimodal >=2 && outcome=='t')||(hybrid_predicition_gshare <= 1 && hybrid_predicition_bimodal <=1 &&  outcome=='n')){
            //update gshare
        }
        else if( (hybrid_predicition_gshare >= 2 && hybrid_predicition_bimodal >=2 && outcome=='n')||(hybrid_predicition_gshare <= 1 && hybrid_predicition_bimodal <=1 &&  outcome=='t')){
            //update gshare
        }
        //gshare correct
        else if((hybrid_predicition_gshare >= 2 && outcome=='t')||(hybrid_predicition_gshare <= 1 &&  outcome=='n')){
            if(chooser_table_val != 3){
                temp++;
            }
            //System.out.println("    CU:" + add + " " + temp);
            mChooseTable.put(address, temp);
        }
        //bimodal correct
        else if( (hybrid_predicition_bimodal >=2 && outcome=='t')||(hybrid_predicition_bimodal <=1 &&  outcome=='n')){
            if(chooser_table_val != 0){
                temp--;
            }
            //System.out.println("    CU:" + add + " " + temp);
            mChooseTable.put(address, temp);
        }

    }
    public void get_stats(){
        number_of_mispredicts= mGshare.getMispredicts()+mBimodal.getMispredicts();
        System.out.println("OUTPUT");
        System.out.println(" number of predictions: "+ (int) total_number_of_predicitions);
        System.out.println(" number of mispredictions: "+ (int) number_of_mispredicts);
        String t=String.format("%.02f",((number_of_mispredicts/total_number_of_predicitions) *100));
        System.out.println(" misprediction rate: "+ t + "%");
        System.out.println("FINAL BIMODAL CONTENTS");
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
            System.out.println(i+ " "+mChooseTable.get(binAddr));
        }
        mGshare.hybrid_get_stats();
        mBimodal.hybrid_get_stats();
    }
}
