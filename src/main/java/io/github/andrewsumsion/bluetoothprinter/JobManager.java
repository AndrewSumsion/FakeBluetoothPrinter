package io.github.andrewsumsion.bluetoothprinter;

import io.github.andrewsumsion.bluetoothprinter.commands.InboundCommand;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JobManager {
    private static JobManager INSTANCE;
    private final Set<JobHandler> handlers = new HashSet<>();
    private final BlockingQueue<PrintingJob> jobs = new LinkedBlockingQueue<>();

    public static JobManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new JobManager();
        }
        return INSTANCE;
    }

    private JobManager() {

    }

    public void registerHandler(JobHandler handler) {
        handlers.add(handler);
    }

    public void removeHandler(JobHandler handler) {
        handlers.remove(handler);
    }

    public void submitJob(PrintingJob job) throws InterruptedException {
        jobs.put(job);
    }

    public void executeJob() throws InterruptedException {
        PrintingJob job = jobs.take();
        for(JobHandler handler : handlers) {
            if(handler instanceof TextJobHandler) {
                if(job instanceof TextPrintingJob) {
                    handler.handle(job.clone());
                }
                continue;
            }
            if(handler instanceof RasterJobHandler) {
                if(job instanceof RasterPrintingJob) {
                    handler.handle(job.clone());
                }
                continue;
            }
            if(handler instanceof OCRJobHandler) {
                if(job instanceof OCRPrintingJob) {
                    handler.handle(job.clone());
                }
                continue;
            }
            handler.handle(job.clone());
        }
    }
}
