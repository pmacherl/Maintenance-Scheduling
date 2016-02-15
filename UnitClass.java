
public class UnitClass {
int capacity,numberOfIntervals,residingAt;

public int getResidingAt() {
	return residingAt;
}

public void setResidingAt(int residingAt) {
	this.residingAt = residingAt;
}

public int getCapacity() {
	return capacity;
}

public void setCapacity(int capacity) {
	this.capacity = capacity;
}

public int getNumberOfIntervals() {
	return numberOfIntervals;
}

public void setNumberOfIntervals(int numberOfIntervals) {
	this.numberOfIntervals = numberOfIntervals;
}

@Override
public String toString() {
	return "UnitClass [capacity=" + capacity + ", numberOfIntervals=" + numberOfIntervals + "]";
}

}


