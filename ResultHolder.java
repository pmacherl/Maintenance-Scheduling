import java.util.ArrayList;

public class ResultHolder {
double dev;
ArrayList<Integer> resAt=new ArrayList<>();
ArrayList<Double> edge=new ArrayList<>();
public ResultHolder(double dev, ArrayList<Integer> resAt, ArrayList<Double> edge) {
	super();
	this.dev = dev;
	this.resAt = resAt;
	this.edge = edge;
}
@Override
public String toString() {
	return "The net reserves in each of the intervals are:"+edge+"\n"+
			"The maintenance of power units are assigned in this fashion"+resAt;
}


}
