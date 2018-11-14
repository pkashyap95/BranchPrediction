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
public class main_predictor {
    bimodal_predictor mBimodalPredictor;
    gshare_predictor  mGsharePredictor;
    hybrid_predictor  mHybridPredictor;
    bp_params param;
    public main_predictor(bp_params parameters){
        param =parameters;
        if(param.bp_name.equalsIgnoreCase("bimodal")){
            mBimodalPredictor = new bimodal_predictor(param.M2);
        }
        else if(param.bp_name.equalsIgnoreCase("gshare")){
            mGsharePredictor = new gshare_predictor(param.M1, param.N);
        }
        else if(param.bp_name.equalsIgnoreCase("hybrid")){
            mHybridPredictor=new hybrid_predictor(param.K, param.M1, param.N, param.M2);
        }
    }
    public void predict(String address, char outcome){
        if(param.bp_name.equalsIgnoreCase("bimodal")){
            mBimodalPredictor.predict(address, outcome);
        }
        else if(param.bp_name.equalsIgnoreCase("gshare")){
            mGsharePredictor.predict(address, outcome);
        }
        else if(param.bp_name.equalsIgnoreCase("hybrid")){
            mHybridPredictor.predict(address, outcome);
        }
    }
    public void get_stats(){
        if(param.bp_name.equalsIgnoreCase("bimodal")){
            mBimodalPredictor.get_stats();
        }
        else if(param.bp_name.equalsIgnoreCase("gshare")){
            mGsharePredictor.get_stats();
        }
        else if(param.bp_name.equalsIgnoreCase("hybrid")){
            mHybridPredictor.get_stats();
        }
    }
    public void test_print(){
        if(param.bp_name.equalsIgnoreCase("bimodal")){
            mBimodalPredictor.test_prints();
        }
        else if(param.bp_name.equalsIgnoreCase("gshare")){
            mGsharePredictor.test_print();
        }
    }
}

