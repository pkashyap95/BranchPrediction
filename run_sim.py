import os
import subprocess
#Powers of 21024, 2048, 4096, 8192,16384, 32768, 65536, 131072, 262144, 524288, 1048576
trace_files = ["../proj2-traces/gcc_trace.txt", "../proj2-traces/perl_trace.txt","../proj2-traces/jpeg_trace.txt"]
###########################################################################
###Modified Experiment 2  Design
output_file4="experiment/experiment_gshare_design_mod.txt"
f = open(output_file4, 'w')
gshare_size=[15,16]
for trace in trace_files:
	for i in gshare_size:
		global_pattern=2;
		while global_pattern <= i:
			java_command="java sim gshare "+str(global_pattern)+" "+str(i)+" "+trace
			print java_command
			f.write(str(subprocess.check_output(java_command, shell=True))+'\n')
			global_pattern=global_pattern+2
f.close()
