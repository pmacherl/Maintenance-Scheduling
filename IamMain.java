import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class IamMain {
static ArrayList<UnitClass> all=new ArrayList<UnitClass>(); //Holds the information of each of the power units
static int total;
static String temp;
static int n;
static double intervals[]; // Max loads estimated in each of the intervals is stored here
static double edge[]; // This is the difference of the power and the max estimated loads in each of the intervals
static double totalPower=0;
static double median; // Ideal net reserve for each of the intervals is stored here
static double toBeOccupied;
static double occupancy;
static int one;
static Random r=new Random();
static int miss=0;
static int entered=0;
static ArrayList<ResultHolder> allResults=new ArrayList<>();
	public static void main(String[] args) throws IOException  {
		// TODO Auto-generated method stub
	
	System.out.println("enter the number of power units");
	Scanner sc=new Scanner(System.in);
	//take i/p from user and store it in reply.
	int reply=sc.nextInt();
	System.out.println("entered:"+reply);
	
	for(int i=0;i<reply;i++){
		UnitClass unit=new UnitClass();
		all.add(unit);
	}
	
	
	System.out.println("Enter the capacity of each of the power units and press enter");
	for(int i=0;i<all.size();i++){
		@SuppressWarnings("resource")
		Scanner scCap=new Scanner(System.in);
		//take i/p from user and store it in reply.
		int cap=scCap.nextInt();
		all.get(i).setCapacity(cap);
	}

	System.out.println("Enter the number of intervals taken by each of the power units and press enter(In the same order as above))");
	for(int i=0;i<all.size();i++){
		@SuppressWarnings("resource")
		Scanner scCap=new Scanner(System.in);
		//take i/p from user and store it in reply.
		int intervals=scCap.nextInt();
		all.get(i).setNumberOfIntervals(intervals);
	}
	
	System.out.println("enter the number of intervals");
	Scanner scCap=new Scanner(System.in);
	//take i/p from user and store it in reply.
	n=sc.nextInt();
	edge=new double[n];
	intervals=new double[n];
	
	System.out.println("Enter the maximum loads estimated in each of the intervals");
	for(int i=0;i<n;i++){
		@SuppressWarnings("resource")
		Scanner scMaxLoad=new Scanner(System.in);
		//take i/p from user and store it in reply.
		double maxLoad=scMaxLoad.nextInt();
		intervals[i]=maxLoad;
	}

for(int i=0;i<all.size();i++){ // total power is calculated taking into account all the power units
	totalPower+=all.get(i).getCapacity();
	toBeOccupied+=all.get(i).getCapacity()*all.get(i).getNumberOfIntervals();
}
for(int i=0;i<n;i++){ // Edge is calculated as mentioned above
	edge[i]=totalPower-intervals[i];
}
for(int i=0;i<n;i++){ // Total occupancy is calculated here, that is summation of all the edges
	occupancy+=edge[i];
}
median=(occupancy-toBeOccupied)/n;

for(int run=0;run<20;run++){
	
for(int r=0;r<all.size();r++){ // 
	all.get(r).setResidingAt(-1);//residingAt is the interval at which the power unit is maintained
} // Is initially set to (-1)
	for(int z=0;z<n;z++){
		edge[z]=totalPower-intervals[z];
	}
	
while(calcUnAssigned()!=0){ // This loop is run until a power unit is given to some or the other interval for maintenance
modify();
}
for(int i=0;i<10000;i++){ // Simulated annealing code!
	
	double tempEdge[]=new double[n];
	for(int j=0;j<n;j++){
		tempEdge[j]=edge[j];
	}
	int tempRes[]=new int[all.size()];
	for(int j=0;j<all.size();j++){
		tempRes[j]=all.get(j).getResidingAt();
	}
	double current=deviation();
	modify();
	double successor=deviation();
	if((successor-current)>0){
		double e=2.71828;
		double power=(successor-current)/i;
		double p=1/Math.pow(e,power);
		double random=(double)r.nextInt(101)/100;
		if(p>random){
			for(int j=0;j<n;j++){
				edge[j]=tempEdge[j];
			}
			for(int j=0;j<all.size();j++){
				all.get(j).setResidingAt(tempRes[j]);
			}

		}
	}
	
}

ArrayList<Integer> resAt=new ArrayList<>();
for(int res=0;res<all.size();res++){
	resAt.add(all.get(res).getResidingAt()+1);
}
ArrayList<Double> edgeList=new ArrayList<>();
for(int res=0;res<n;res++){
edgeList.add(edge[res]);
}
ResultHolder rh=new ResultHolder(deviation(),resAt,edgeList);
allResults.add(rh);
	}


Collections.sort(allResults, new Comparator<ResultHolder>() {
    @Override
    public int compare(ResultHolder lhs, ResultHolder rhs) {
        int res;
    	if(lhs.dev<rhs.dev){
        	res=-1;
        }
    	else res=1;
        return res;
    }
});


System.out.println(allResults.get(0).toString()); // Printing the results!

	
	} //main ends
	static double deviation(){ // This is the heuristic for this problem. Calculates how much each of the interval deviates from the median and adds them
		double deviation=0;
		for(int i=0;i<n;i++){
			deviation+=Math.abs(edge[i]-median);		
			}
		return deviation;
	}
	
	static int calcUnAssigned(){// This checks and returns how many power units are unassigned. 
		int count=0;
for(int i=0;i<all.size();i++){
	if(all.get(i).getResidingAt()==-1){
		count++;
	}
	
	}
return count;
}
	static void modify(){ //Making a move!
		one=r.nextInt(3)+1;
		if(one%2==0){
		int pickFromU=r.nextInt(all.size());
	
		int pickFromI=r.nextInt(n);
		if(edge[pickFromI]>all.get(pickFromU).getCapacity()){
		
			if(all.get(pickFromU).getNumberOfIntervals()==1){
			
				if(all.get(pickFromU).getResidingAt()!=-1){
					edge[all.get(pickFromU).getResidingAt()]+=all.get(pickFromU).getCapacity();
				}
			all.get(pickFromU).setResidingAt(pickFromI);
			edge[pickFromI]-=all.get(pickFromU).getCapacity();
		
			}
		else{
			int decider=0;
			if((pickFromI-1)>=0){
			if(edge[pickFromI-1]>all.get(pickFromU).getCapacity()){
				decider+=5;
			}	
			}
			if((pickFromI+1)<n){
				if(edge[pickFromI+1]>all.get(pickFromU).getCapacity()){
				decider+=10;
				}
			}
			if(decider==15){
				if(r.nextBoolean()){
					if(all.get(pickFromU).getResidingAt()!=-1){
						edge[all.get(pickFromU).getResidingAt()]+=all.get(pickFromU).getCapacity();
						edge[all.get(pickFromU).getResidingAt()+1]+=all.get(pickFromU).getCapacity();
					}
					all.get(pickFromU).setResidingAt(pickFromI-1);
					edge[pickFromI]-=all.get(pickFromU).getCapacity();
					edge[pickFromI-1]-=all.get(pickFromU).getCapacity();
				}
				else{
					
					if(all.get(pickFromU).getResidingAt()!=-1){
						edge[all.get(pickFromU).getResidingAt()]+=all.get(pickFromU).getCapacity();
						edge[all.get(pickFromU).getResidingAt()+1]+=all.get(pickFromU).getCapacity();
					}
					all.get(pickFromU).setResidingAt(pickFromI);
					edge[pickFromI]-=all.get(pickFromU).getCapacity();
					edge[pickFromI+1]-=all.get(pickFromU).getCapacity();
				}
			}
			else if(decider==5){
				if(all.get(pickFromU).getResidingAt()!=-1){
					edge[all.get(pickFromU).getResidingAt()]+=all.get(pickFromU).getCapacity();
					edge[all.get(pickFromU).getResidingAt()+1]+=all.get(pickFromU).getCapacity();
				}
				all.get(pickFromU).setResidingAt(pickFromI-1);
				edge[pickFromI]-=all.get(pickFromU).getCapacity();
				edge[pickFromI-1]-=all.get(pickFromU).getCapacity();
			}
			else if(decider==10){
				if(all.get(pickFromU).getResidingAt()!=-1){
					edge[all.get(pickFromU).getResidingAt()]+=all.get(pickFromU).getCapacity();
					edge[all.get(pickFromU).getResidingAt()+1]+=all.get(pickFromU).getCapacity();
				}
				all.get(pickFromU).setResidingAt(pickFromI);
				edge[pickFromI]-=all.get(pickFromU).getCapacity();
				edge[pickFromI+1]-=all.get(pickFromU).getCapacity();
			}
		}
		}
		}

		else{
			int pick1=0,pick2=0;
			while(pick1==pick2){
				pick1=r.nextInt(all.size());
				pick2=r.nextInt(all.size());
			}
			if(all.get(pick1).getResidingAt()!=-1&&all.get(pick2).getResidingAt()!=-1){
			if(all.get(pick1).getNumberOfIntervals()==2||all.get(pick2).getNumberOfIntervals()==2){				
				if(all.get(pick1).getNumberOfIntervals()==2&&all.get(pick2).getNumberOfIntervals()==1){
					if(all.get(pick2).getResidingAt()!=all.get(pick1).getResidingAt()&&all.get(pick2).getResidingAt()!=(all.get(pick1).getResidingAt()+1)) {
						//code goes here!
						dualUnit(pick1,pick2);
					}
				}
				if(all.get(pick2).getNumberOfIntervals()==2&&all.get(pick1).getNumberOfIntervals()==1){
					int temp=pick2;
					pick2=pick1;
					pick1=temp;
					
					if(all.get(pick2).getResidingAt()!=all.get(pick1).getResidingAt()&&all.get(pick2).getResidingAt()!=(all.get(pick1).getResidingAt()+1)) {
						//code goes here!
						dualUnit(pick1,pick2);
					}
				
				}
				if(all.get(pick1).getNumberOfIntervals()==2&&all.get(pick2).getNumberOfIntervals()==2){
					if((all.get(pick1).getCapacity()<edge[all.get(pick2).getResidingAt()]+all.get(pick2).getCapacity())&&all.get(pick1).getCapacity()<edge[all.get(pick2).getResidingAt()+1]+all.get(pick2).getCapacity()){
						if((all.get(pick2).getCapacity()<edge[all.get(pick1).getResidingAt()]+all.get(pick1).getCapacity())&&all.get(pick2).getCapacity()<edge[all.get(pick1).getResidingAt()+1]+all.get(pick1).getCapacity()){
							edge[all.get(pick1).getResidingAt()]+=all.get(pick1).getCapacity();
							edge[all.get(pick1).getResidingAt()+1]+=all.get(pick1).getCapacity();
							edge[all.get(pick2).getResidingAt()]+=all.get(pick2).getCapacity();
							edge[all.get(pick2).getResidingAt()+1]+=all.get(pick2).getCapacity();
							
							edge[all.get(pick1).getResidingAt()]-=all.get(pick2).getCapacity();
							edge[all.get(pick1).getResidingAt()+1]-=all.get(pick2).getCapacity();
							edge[all.get(pick2).getResidingAt()]-=all.get(pick1).getCapacity();
							edge[all.get(pick2).getResidingAt()+1]-=all.get(pick1).getCapacity();
							
							int temp=all.get(pick2).getResidingAt();
							all.get(pick2).setResidingAt(all.get(pick1).getResidingAt());
							all.get(pick1).setResidingAt(temp);
							
						}	
					}
				}
			}
			else{
		if(all.get(pick1).getResidingAt()!=all.get(pick2).getResidingAt()){
		double temp1=edge[all.get(pick1).getResidingAt()]+all.get(pick1).getCapacity()-all.get(pick2).getCapacity();
		double temp2=edge[all.get(pick2).getResidingAt()]+all.get(pick2).getCapacity()-all.get(pick1).getCapacity();
		if(temp1>=0&&temp2>=0){
			edge[all.get(pick1).getResidingAt()]=temp1;
			edge[all.get(pick2).getResidingAt()]=temp2;
			int temp=all.get(pick1).getResidingAt();
			all.get(pick1).setResidingAt(all.get(pick2).getResidingAt());
			all.get(pick2).setResidingAt(temp);
			
		}
		}
		}
			}
		}
	}
	static void dualUnit(int two,int one){ // What to do when the randomized picker picks two power units with the interval length 2.
					
		if(all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()]+all.get(two).getCapacity())||all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()+1]+all.get(two).getCapacity())){
		if(all.get(one).getResidingAt()==0){
		

			if(all.get(two).getCapacity()<(edge[all.get(one).getResidingAt()]+all.get(one).getCapacity())&&all.get(two).getCapacity()<edge[all.get(one).getResidingAt()+1]){
			
				if(all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()]+all.get(two).getCapacity())&&all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()+1]+all.get(two).getCapacity())){
					boolean random=r.nextBoolean();
					if(random){
						
						
						edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
						edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
						
						edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
						
						edge[all.get(two).getResidingAt()]-=all.get(one).getCapacity();
						
						
						edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
						edge[all.get(one).getResidingAt()+1]-=all.get(two).getCapacity();
						
						
						all.get(one).setResidingAt(all.get(two).getResidingAt());
						all.get(two).setResidingAt(0);
						
					}
					else{
						edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
						edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
						
						edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
						
						edge[all.get(two).getResidingAt()+1]-=all.get(one).getCapacity();
						
						
						edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
						edge[all.get(one).getResidingAt()+1]-=all.get(two).getCapacity();
						
						
						all.get(one).setResidingAt(all.get(two).getResidingAt()+1);
						all.get(two).setResidingAt(0);
					}
					
				}
				else if(all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()]+all.get(two).getCapacity())){
					
					edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
					edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
					
					edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
					
					edge[all.get(two).getResidingAt()]-=all.get(one).getCapacity();
					
					
					edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
					edge[all.get(one).getResidingAt()+1]-=all.get(two).getCapacity();
					
					
					all.get(one).setResidingAt(all.get(two).getResidingAt());
					all.get(two).setResidingAt(0);
				}
				else{
					
					edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
					edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
					
					edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
					
					edge[all.get(two).getResidingAt()+1]-=all.get(one).getCapacity();
					
					
					edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
					edge[all.get(one).getResidingAt()+1]-=all.get(two).getCapacity();
					
					
					all.get(one).setResidingAt(all.get(two).getResidingAt()+1);
					all.get(two).setResidingAt(0);
				}
			}
		}
		else if(all.get(one).getResidingAt()==(n-1)){
			if(all.get(two).getCapacity()<(edge[all.get(one).getResidingAt()]+all.get(one).getCapacity())&&all.get(two).getCapacity()<edge[all.get(one).getResidingAt()-1]){
				
				if(all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()]+all.get(two).getCapacity())&&all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()+1]+all.get(two).getCapacity())){
					boolean random=r.nextBoolean();
					if(random){
						
						
						edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
						edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
						
						edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
						
						edge[all.get(two).getResidingAt()]-=all.get(one).getCapacity();
						
						
						edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
						edge[all.get(one).getResidingAt()-1]-=all.get(two).getCapacity();
						
						
						all.get(one).setResidingAt(all.get(two).getResidingAt());
						all.get(two).setResidingAt(n-2);
						
					}
					else{
						edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
						edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
						
						edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
						
						edge[all.get(two).getResidingAt()+1]-=all.get(one).getCapacity();
						
						
						edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
						edge[all.get(one).getResidingAt()-1]-=all.get(two).getCapacity();
						
						
						all.get(one).setResidingAt(all.get(two).getResidingAt()+1);
						all.get(two).setResidingAt(n-2);
					}
					
				}
				else if(all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()]+all.get(two).getCapacity())){
					edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
						edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
						
						edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
						
						edge[all.get(two).getResidingAt()]-=all.get(one).getCapacity();
						
						
						edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
						edge[all.get(one).getResidingAt()-1]-=all.get(two).getCapacity();
						
						
						all.get(one).setResidingAt(all.get(two).getResidingAt());
						all.get(two).setResidingAt(n-2);
				}
				else{
					
						edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
						edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
						
						edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
						
						edge[all.get(two).getResidingAt()+1]-=all.get(one).getCapacity();
						
						
						edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
						edge[all.get(one).getResidingAt()-1]-=all.get(two).getCapacity();
						
						
						all.get(one).setResidingAt(all.get(two).getResidingAt()+1);
						all.get(two).setResidingAt(n-2);
				}
			}
		}
		else{
			boolean random1=r.nextBoolean();
			boolean flag=false;
			if(random1){
				if(all.get(two).getCapacity()<(edge[all.get(one).getResidingAt()]+all.get(one).getCapacity())&&all.get(two).getCapacity()<edge[all.get(one).getResidingAt()+1]){
					
					if(all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()]+all.get(two).getCapacity())&&all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()+1]+all.get(two).getCapacity())){
						boolean random=r.nextBoolean();
						if(random){
							
							
							edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
							edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
							
							edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
							
							edge[all.get(two).getResidingAt()]-=all.get(one).getCapacity();
							
							
							edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
							edge[all.get(one).getResidingAt()+1]-=all.get(two).getCapacity();
							
							int temp=all.get(one).getResidingAt();
							all.get(one).setResidingAt(all.get(two).getResidingAt());
							all.get(two).setResidingAt(temp);
							flag=true;
							
						}
						else{
							edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
							edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
							
							edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
							
							edge[all.get(two).getResidingAt()+1]-=all.get(one).getCapacity();
							
							
							edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
							edge[all.get(one).getResidingAt()+1]-=all.get(two).getCapacity();
							

							int temp=all.get(one).getResidingAt();
							all.get(one).setResidingAt(all.get(two).getResidingAt()+1);
							all.get(two).setResidingAt(temp);
							flag=true;
						}
						
					}
					else if(all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()]+all.get(two).getCapacity())){
						
						edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
						edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
						
						edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
						
						edge[all.get(two).getResidingAt()]-=all.get(one).getCapacity();
						
						
						edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
						edge[all.get(one).getResidingAt()+1]-=all.get(two).getCapacity();
						
						int temp=all.get(one).getResidingAt();
						all.get(one).setResidingAt(all.get(two).getResidingAt());
						all.get(two).setResidingAt(temp);
						flag=true;
					}
					else{
						
						edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
						edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
						
						edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
						
						edge[all.get(two).getResidingAt()+1]-=all.get(one).getCapacity();
						
						
						edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
						edge[all.get(one).getResidingAt()+1]-=all.get(two).getCapacity();
						
						
						int temp=all.get(one).getResidingAt();
						all.get(one).setResidingAt(all.get(two).getResidingAt()+1);
						all.get(two).setResidingAt(temp);
						flag=true;
					}
				}
			}
			if(!random1&&!flag){
				if(all.get(two).getCapacity()<(edge[all.get(one).getResidingAt()]+all.get(one).getCapacity())&&all.get(two).getCapacity()<edge[all.get(one).getResidingAt()-1]){
					
					if(all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()]+all.get(two).getCapacity())&&all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()+1]+all.get(two).getCapacity())){
						boolean random=r.nextBoolean();
						if(random){
							
							
							edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
							edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
							
							edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
							
							edge[all.get(two).getResidingAt()]-=all.get(one).getCapacity();
							
							
							edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
							edge[all.get(one).getResidingAt()-1]-=all.get(two).getCapacity();
							
							int temp=all.get(one).getResidingAt();
							all.get(one).setResidingAt(all.get(two).getResidingAt());
							all.get(two).setResidingAt(temp-1);
							
						}
						else{
							edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
							edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
							
							edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
							
							edge[all.get(two).getResidingAt()+1]-=all.get(one).getCapacity();
							
							
							edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
							edge[all.get(one).getResidingAt()-1]-=all.get(two).getCapacity();
							
							int temp=all.get(one).getResidingAt();
							all.get(one).setResidingAt(all.get(two).getResidingAt()+1);
							all.get(two).setResidingAt(temp-1);
						}
						
					}
					else if(all.get(one).getCapacity()<(edge[all.get(two).getResidingAt()]+all.get(two).getCapacity())){
						edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
							edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
							
							edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
							
							edge[all.get(two).getResidingAt()]-=all.get(one).getCapacity();
							
							
							edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
							edge[all.get(one).getResidingAt()-1]-=all.get(two).getCapacity();
							
							int temp=all.get(one).getResidingAt();
							all.get(one).setResidingAt(all.get(two).getResidingAt());
							all.get(two).setResidingAt(temp-1);
					}
					else{
						
							edge[all.get(two).getResidingAt()]+=all.get(two).getCapacity();
							edge[all.get(two).getResidingAt()+1]+=all.get(two).getCapacity();
							
							edge[all.get(one).getResidingAt()]+=all.get(one).getCapacity();
							
							edge[all.get(two).getResidingAt()+1]-=all.get(one).getCapacity();
							
							
							edge[all.get(one).getResidingAt()]-=all.get(two).getCapacity();
							edge[all.get(one).getResidingAt()-1]-=all.get(two).getCapacity();
							
							int temp=all.get(one).getResidingAt();
							all.get(one).setResidingAt(all.get(two).getResidingAt()+1);
							all.get(two).setResidingAt(temp-1);
					}
				}
			}
		}
		}
	}
}
