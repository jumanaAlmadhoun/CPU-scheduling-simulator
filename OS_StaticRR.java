
import java.io.*;
import java.util.*;

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

public class OS_StaticRR {

    static Queue<JobNode> allValidJobs = new LinkedList<JobNode>(); // all valid jobs
    static Queue<JobNode> shortestQueue = new LinkedList<JobNode>(); // /shortest queue (ready queue)
    static Queue<JobNode> longestQueue = new LinkedList<JobNode>(); // longest queue
    static Queue<JobNode> holdQueue3 = new LinkedList<JobNode>(); // waiting queue
    static LinkedList<JobNode> completeQueue = new LinkedList<JobNode>(); // compeleted jobs
    static JobNode executingJob; // process currently executing in CPU
    public static int startTime; // system start time
    public static int mainMemory; // M: system main memory
    public static int devices; // S: system total devices
    public static int currentTime; // system current time
    public static int availableMemory; // available main memory
    public static int availableDevices; // available devices
    public static int JobsNo; // number of jobs
    public static int quantum = 7; // 5+TN which is 2, so total of 7
    public static int i; //for external events
    public static int e; //for internal events
    public static int AvgBT = 0; // Average Burst Time of ready queue, used while distributing the jobs among the queues
    public static int maximum_value = Integer.MAX_VALUE;

    public static void main(String[] args) throws FileNotFoundException {

        //Read the input file
        File file = new File("input3.txt");
        if (!file.exists()) {
            System.out.println(file.getName() + " file does not exist, please try again after uploading them.");
            System.exit(1);
        }
        //create Scanner for file content
        Scanner input = new Scanner(file);
        File outputFile = new File("Output3.txt");
        PrintWriter output = new PrintWriter(outputFile);
        String line;
        String[] command;
        // start reading from the input file 

        //While we still have values in the file, keep reading
        while (input.hasNext()) {
            // read line by line from the input file remove the '=' sign and all the letters 
            line = input.nextLine().replaceAll("[A-Z]=", "");
            // separate the line into an array
            command = line.split(" ");
            //------------------------------------------------------------------
            // read first letter to decide what section to go to.
            switch (command[0]) {
                //--------------------------------------------------------------
                case "C":
                    //initiate the memory and the devices
                    systemConfiguration(command);
                    break;
                //--------------------------------------------------------------
                case "A":
                    //Add the job to the "allValidJob" queue if it is valid (have less or equal values of memory and devices)
                    addJobs(command);
                    break;
                case "D":
                    //print a screenshot of the system at some point of time
                    int time = Integer.parseInt(command[1]);
                    if (time != 999999) {
                        // print the state of the system at a specified time
                        allValidJobs.add(new JobNode(time));
                    } else {
                        //------------------------------------------------------
                        //Take the first job from "allValidJob" and put it ready to be executed
                        JobNode first_job = allValidJobs.poll();
                        currentTime = first_job.getArrivingTime();
                        availableMemory = mainMemory - first_job.getNumOfMemoryUnit();
                        availableDevices = devices - first_job.getNumOfDevices();
                        executingJob = first_job;
                        executingJob.setStartTime(currentTime);
                        executingJob.setFinishTime(currentTime + quantum);
                        //------------------------------------------------------
                        // As long as not all job are in the complete queue, keep going
                        while (JobsNo != completeQueue.size()) {
                            // i equals the burst time of the next job unless "allVaidQueue" is empty, the it's infinity
                            if (allValidJobs.isEmpty()) {
                                i = maximum_value;
                            } else {
                                i = allValidJobs.peek().getArrivingTime();
                            }

                            // e equals the finish time of the current executing job unless there is no job to be executed then the it's infinity
                            if (executingJob == null) {
                                e = maximum_value;
                            } else {
                                e = executingJob.getFinishTime();
                            }
                            //--------------------------------------------------
                            // Start with the closest time
                            currentTime = Math.min(e, i);
                            //--------------------------------------------------
                            if (i < e) {
                                // Start external event first since it's closer
                                externalEvent(output);
                            } else if (i > e) {
                                // Start internal event first since it's closer
                                internalEvent();
                            } else {
                                // if i == e
                                // perform internal events before external events
                                internalEvent();
                                externalEvent(output);
                            }
                        } // end of while loop
                        //------------------------------------------------------
                        // print system final state and reset variables
                        if (time == 999999 && completeQueue.size() == JobsNo) {
                            finalSnapShot(output);
                            completeQueue.clear();
                            shortestQueue.clear();
                            longestQueue.clear();
                            allValidJobs.clear();
                            executingJob = null;
                            startTime = 0;
                            currentTime = 0;
                            JobsNo = 0;
                            time = 0;
                        }
                    }
                    break;
            }
        }

        //Finish the whole system
        input.close();
        output.close();

    }

    //---------------------------- Initate Input -------------------------------
    public static void addJobs(String[] command) {
        // ---- add a new job into the "allValidJob" queue if there is enough memory and devices ---
        //take the necessary information about the job
        int arrival_time = Integer.parseInt(command[1]);
        int job_id = Integer.parseInt(command[2]);
        int requested_mm = Integer.parseInt(command[3]);
        int requested_d = Integer.parseInt(command[4]);
        int burst_time = Integer.parseInt(command[5]);
        int job_priority = Integer.parseInt(command[6]);
        // create process if the job is valid then add it to allValidJob queue
        if (requested_mm <= mainMemory && requested_d <= devices) {
            allValidJobs.add(new JobNode(arrival_time, job_id,
                    requested_mm, requested_d, burst_time, job_priority));
            JobsNo++;
        }
    }

    public static void systemConfiguration(String[] command) {
        //initiate the current system memory and devices
        startTime = Integer.parseInt(command[1]);
        currentTime = startTime;
        mainMemory = Integer.parseInt(command[2]);
        devices = Integer.parseInt(command[3]);
        availableMemory = mainMemory;
        availableDevices = devices;
    }

    //---------------------------------Task 0-----------------------------------
    public static void externalEvent(PrintWriter write) {
        // work on allValidJobs queue
        if (!allValidJobs.isEmpty()) {
            //if not empty take a job
            JobNode job = allValidJobs.poll();
            //check if the 1st job is a "D" job (D jobs have the ID as 0)
            if (job.getJobID() == 0) {
                //print a snapshot of the current system
                snapShot(write, job);
            } else if (job.getNumOfDevices() > availableDevices
                    || job.getNumOfMemoryUnit() > availableMemory) {
                //if the job exceeded the current avaliable recources, assign it in queue3
                holdQueue3.add(job);
            } else {
                if (shortestQueue.isEmpty()) {
                    //if queue 1 is empty, AvgBT equal the current job burst time.
                    AvgBT = job.getbusrtTime();
                } else {
                    //if queue 1 is not empty, AvgBT equal the average burst time of the processes in queue 1
                    computeAvgBT();
                }

                //add the job inside the right queue
                if (job.getbusrtTime() > AvgBT) {
                    longestQueue.add(job);
                } else {
                    shortestQueue.add(job);
                    resuceResources(job.getNumOfMemoryUnit(), job.getNumOfDevices()); //take the resouces
                }

            }

        }
    }

    //------------------------------INTERNAL EVENT / WORK AS CPU----------------
    public static void internalEvent() {
        //Execute the job according to either the available quantum or its need time; the minimum one
        executingJob.setNeedTime(executingJob.getNeedTime() - quantum);

        //check if the job is finished
        if (executingJob.getNeedTime() <= 0) {
            //return the memory and devices
            releaseResourcesCPU();

            //take a new job if Q1 is not empty
            if (!shortestQueue.isEmpty()) {
                //take a new job from shortest queue
                executingJob = shortestQueue.poll();
                //determine the job start time of the job as the current time
                executingJob.setStartTime(currentTime);
                // determine the waiting time as the different between the arriving time and start time
                executingJob.setWaitingTime(executingJob.getStartTime() - executingJob.getArrivingTime());
                //set the finish time of the job
                int finish = Math.min(executingJob.getNeedTime(), quantum);
                executingJob.setFinishTime(currentTime + finish);
                //update AvgBT and AR and SR
                computeAvgBT();
            }
        } else {
            //else, the job still have burst time, start staic round robin 
            staticRoundRobin();
        }
    }

    //--------------------------- Task 1 ---------------------------------------
    public static void moveJobFromQueue3() {
        if (!holdQueue3.isEmpty()) {
            //turn the queue into array (easier to deal with)
            Object[] queue3 = holdQueue3.toArray();

            //for all the jobs in queue 3, check if it have less or equal memory and devices to be moved to other queues
            for (int j = 0; j < queue3.length; j++) {
                JobNode job = (JobNode) queue3[j];

                //if we have enough memory and devices for this job
                if (availableMemory >= job.getNumOfMemoryUnit()
                        && availableDevices >= job.getNumOfDevices()) {

                    //compute the average
                    computeAvgBT();
                    //if less or equal AvgBT, add to queue1
                    if (job.getbusrtTime() <= AvgBT) {
                        holdQueue3.remove(job);
                        shortestQueue.add(job);
                        resuceResources(job.getNumOfMemoryUnit(), job.getNumOfDevices());
                    } else {
                        //add to queue 2
                        longestQueue.add(job);
                        holdQueue3.remove(job);
                    }
                }
            }
        }
    }

    //----------------------------Task2 ----------------------------------------
    public static void moveJobFromQueue2() {
        if (!longestQueue.isEmpty()) {
            int AvgWait = 0;
            int ProcessiDPrt = 0;
            //sum all the waiting time od the jobs in queue2
            for (JobNode p : longestQueue) {
                p.setWaitingTime(currentTime - p.getArrivingTime());
                AvgWait += p.getWaitingTime();
            }
            // compute the average waiting time
            AvgWait = AvgWait / longestQueue.size();

            //compute the dynamic priority of the jobs in queue 2
            for (JobNode p : longestQueue) {
                ProcessiDPrt = (int) (((p.getWaitingTime() - AvgWait) * 0.2)
                        + (p.getPriority() * 0.8));
                p.setDynamicPriority(ProcessiDPrt);
            }
            //sort queue 2 according to its new dynamic priority 
            sortLongestQueue();

            //take the first job after sorting and add t to queue 1
            JobNode job = longestQueue.poll();
            shortestQueue.add(job);
            longestQueue.remove(job);
            //update the recources and AR and SR
            resuceResources(job.getNumOfMemoryUnit(), job.getNumOfDevices());
        }
    }

    //---------------------------------To sort Queue 2--------------------------
    public static void sortLongestQueue() {
        Object[] longest = longestQueue.toArray();
        //Sorting (using bubble sort)
        for (int j = 0; j < longest.length; j++) {
            JobNode key = (JobNode) longest[j];
            int k = j - 1;
            while (k >= 0 && ((JobNode) longest[k]).getDynamicPriority()
                    < key.getDynamicPriority()) {
                longest[k + 1] = longest[k];
                k = k - 1;
            }
            longest[k + 1] = key;
        }

        //add the new sorted values inside Q3 after sorting them
        longestQueue.clear();
        for (Object longest1 : longest) {
            longestQueue.add((JobNode) longest1);
        }
    }

    //---------------------------------FREE CPU---------------------------------
    public static void releaseResourcesCPU() {
        // release memory and devices
        availableMemory += executingJob.getNumOfMemoryUnit();
        availableDevices += executingJob.getNumOfDevices();
        // add the finished job to complete queue 
        completeQueue.add(executingJob);
        // no jobs in CPU
        executingJob = null;
        // move processes from HQ2 to HQ1 and From HQ3 to HQ1 OR HQ2 if possible
        moveJobFromQueue3();
        moveJobFromQueue2();
    }

    //--------------------------------------------------------------------------
    public static void resuceResources(int NumOfMemoryUnit, int NumOfDevices) {
        //take the recouces from the availbe recources
        availableMemory -= NumOfMemoryUnit;
        availableDevices -= NumOfDevices;
    }

    //----------------------------DYNAMIC ROUND ROBIN---------------------------
    public static void staticRoundRobin() {
        if (shortestQueue.isEmpty()) {
            // if there is no jobs in hold queue 1
            // set the job start time of execution
            executingJob.setStartTime(currentTime);
            // set the executing job finish time
            int finish = Math.min(executingJob.getNeedTime(), quantum);
            executingJob.setFinishTime(currentTime + finish);
        } else {
            //add the job again into the shortest queue
            shortestQueue.add(executingJob);
            //take a new job to execute
            JobNode p = shortestQueue.poll();
            executingJob = p;
            //set its start time as current time
            executingJob.setStartTime(currentTime);
            // set the finish time
            int finish = Math.min(executingJob.getNeedTime(), quantum);
            executingJob.setFinishTime(currentTime + finish);
        }
    }

    //--------------------------------------------------------------------------
    public static void computeAvgBT() {
        if (!shortestQueue.isEmpty()) {
            AvgBT = 0;
            int allBurst = 0;
            //sum all the burst time of the jobs
            for (JobNode job : shortestQueue) {
                allBurst += job.getbusrtTime();
            }
            //compute the average
            AvgBT = (allBurst / shortestQueue.size());

        }
    }

    //------------------------PRINT CURRENT SYSTEM STATE------------------------
    public static void snapShot(PrintWriter write, JobNode job) {
        write.println("\n<< At time " + job.getArrivingTime() + ":");
        write.println("  Current Available Main Memory = " + availableMemory);
        write.println("  Current Devices               = " + availableDevices);
        write.println("\n  Completed jobs: \n  ----------------");
        write.println("  Job ID   Arrival Time    Finish Time  Turnaround Time \n"
                + "  =================================================================");
        Collections.sort(completeQueue, new sortbyID());
        for (JobNode p : completeQueue) {
            write.printf(p.toString());
        }
        //----------------------------------------------------------------------
        write.println();
        write.println("\n\n  Hold Queue 3: \n  ----------------");
        for (JobNode p : holdQueue3) {
            write.printf("%6d", p.getJobID());
        }
        //----------------------------------------------------------------------
        write.println();
        write.println("\n\n  Hold Queue 2: \n  ----------------");
        for (JobNode p : longestQueue) {
            write.printf("%6d", p.getJobID());
        }
        //----------------------------------------------------------------------
        write.println();
        write.println("\n\n  Hold Queue1 (Ready Queue): \n  ----------------");
        write.println("  JobID    NeedTime    Total Execution Time \n"
                + "  ===============================");
        for (JobNode p : shortestQueue) {
            write.printf("%5d%10d%15d\n\n", p.getJobID(), p.getNeedTime(),
                    (p.getbusrtTime() - p.getNeedTime()));
        }

        write.println("\n\n  Process running on the CPU: \n  ----------------------------");
        write.println("  Job ID   NeedTime    Total Execution Time");
        write.printf("%5d%10d%15d\n\n\n", executingJob.getJobID(), executingJob.getNeedTime(),
                (executingJob.getbusrtTime() - executingJob.getNeedTime()));
    }

    //-------------------------PRINT FINAL SYSTEM STATE-------------------------
    public static void finalSnapShot(PrintWriter write) throws FileNotFoundException {
        write.println("<< Final state of system: ");
        write.println("  Current Available Main Memory = " + availableMemory);
        write.println("  Current Devices               = " + availableDevices);
        write.println("\n  Completed jobs: \n" + "  ----------------");
        write.println("  Job ID   Arrival Time    Finish Time  Turnaround Time ");
        write.println("  =================================================================");
        double avr_trunaround = 0;
        Collections.sort(completeQueue, new sortbyID());
        for (JobNode p : completeQueue) {
            write.printf(p.toString());
            p.setTurnaroundTime(p.getFinishTime() - p.getArrivingTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getbusrtTime());
            //System.out.println("job ID: " + p.getJobID() + "  TurnaroundTime: " + p.getTurnAT() + "  waitingTime: " + p.getWaitingTime());
            avr_trunaround += p.getTurnaroundTime();
        }
        write.printf("\n\n  System Turnaround Time =  %.3f\n\n", avr_trunaround / completeQueue.size());
        write.println("\n*********************************************************************\n");
    }

}

//---------------------------------SORTING--------------------------------------
class sortbyID_ implements Comparator<JobNode> {

    // Used for sorting jobs in ascending order
    @Override
    public int compare(JobNode a, JobNode b) {
        return (int) (a.getJobID() - b.getJobID());
    }
}
