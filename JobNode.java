/*Student ID: 1906145, Student Name: Nojood Alghamdi
Inter Core i5 7th generation, 8 GB RAM, 64- bit operating system Windows 10 
1 TB SSD
--------------------------------------------------------------------------------
Student ID: 1906580, Student Name: Reem Altamimi
Intel Core i7 10th Generation, 12 GB RAM, 64-bit operating system Windows 10 
128 GB SSD, 1 Terabyte HDD
--------------------------------------------------------------------------------
Student ID: 1905255, Student Name: Maha Altwaim
AMD Ryzen 7-3750H, 16 GB RAM,64 - bit Operating system Windows 10 
512 GB SSD- 1 TB HDD
--------------------------------------------------------------------------------
Student ID: 1911188, Student Name: Jumana Almadhoun
Inter Core i7 7th generation, 16 GB RAM, 64- bit operating system Windows 10 
512 GB SSD
--------------------------------------------------------------------------------
Student ID: 1905872, Student Name:  Jawaherah Alsefri
Intel Core i7 10th Generation, 16 GB RAM, 64-bit operating system Windows 10 
1 TB SSD
 */
public class JobNode {

    private int arrivingTime;
    private int jobID;  //J
    private int numOfMemoryUnit;  //M
    private int numOfDevices;  //S
    private int priority;  //P
    private int busrtTime;  //R
    private int startTime;
    private int turnaroundTime;
    private int finishTime;
    private int waitingTime;
    private int weight;
    private int dynamicPriority;
    private int NeedTime;

    // "A" job constructor
    JobNode(int arrivingTime, int jobNumber, int numOfMemoryUnit, int numOfDevices, int busrtTime, int priority) {
        this.arrivingTime = arrivingTime;
        this.jobID = jobNumber;
        this.numOfMemoryUnit = numOfMemoryUnit;
        this.numOfDevices = numOfDevices;
        this.busrtTime = busrtTime;
        this.priority = priority;
        this.NeedTime = this.busrtTime;
        // set process weight according to its priority
        this.weight = this.priority == 1 ? 2 : 1;

    }

    // "D" job constructor
    JobNode(int time) {
        this.arrivingTime = time;
        this.jobID = 0;
    }

    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobNumber) {
        this.jobID = jobNumber;
    }

    public int getWeight() {
        return weight;
    }

    public int getNeedTime() {
        return NeedTime;
    }

    public void setNeedTime(int NeedTime) {
        this.NeedTime = NeedTime;
    }

    public int getTurnaroundTime() {
        return this.finishTime - this.arrivingTime;
    }

    public void setTurnaroundTime(int turnAroundTime) {
        this.turnaroundTime = turnAroundTime;
    }

    public int getbusrtTime() {
        return busrtTime;
    }

    public void setbusrtTime(int busrtTime) {
        this.busrtTime = busrtTime;
    }

    public int getArrivingTime() {
        return arrivingTime;
    }

    public void setArrivingTime(int arrivingTime) {
        this.arrivingTime = arrivingTime;
    }

    public int getNumOfMemoryUnit() {
        return numOfMemoryUnit;
    }

    public void setNumOfMemoryUnit(int requestedMemory) {
        this.numOfMemoryUnit = requestedMemory;
    }

    public int getNumOfDevices() {
        return numOfDevices;
    }

    public void setNumOfDevices(int requestedDevice) {
        this.numOfDevices = requestedDevice;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getDynamicPriority() {
        return dynamicPriority;
    }

    public void setDynamicPriority(int dynamicPriority) {
        this.dynamicPriority = dynamicPriority;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public String toString() {
        return String.format("%5d%11d%17d%15d\n", jobID, arrivingTime,
                finishTime, (this.finishTime - this.arrivingTime));
    }
}
