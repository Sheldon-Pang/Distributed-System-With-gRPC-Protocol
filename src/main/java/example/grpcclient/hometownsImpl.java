package example.grpcclient;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;

import java.util.LinkedList;

class hometownsImpl extends HometownsGrpc.HometownsImplBase {
    public LinkedList<Hometown> hometownList = new LinkedList<>();

    @Override
    public void write(HometownsWriteRequest req, StreamObserver<HometownsWriteResponse> responseObserver) {
        String name =  req.getHometown().getName();
        String city = req.getHometown().getCity();
        String region =  req.getHometown().getRegion();
        System.out.println("Received from client: " + name + " " + city + " " + region);
        HometownsWriteResponse.Builder response = HometownsWriteResponse.newBuilder();

        try {
            Hometown.Builder addHometown = Hometown.newBuilder();
            addHometown.setName(name);
            addHometown.setCity(city);
            addHometown.setRegion(region);

            Hometown finalHometown = addHometown.build();
            hometownList.add(finalHometown);
            System.out.println("Hometown has been saved.");
            response.setIsSuccess(true);
        } catch (Exception e) {
            response.setIsSuccess(false);
            response.setError("Unable to save hometown info.");
        }

        HometownsWriteResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
    @Override
    public void search(HometownsSearchRequest req, StreamObserver<HometownsReadResponse> responseObserver) {
        String city =  req.getCity();
        System.out.println("Received from client: " + city);
        HometownsReadResponse.Builder response = HometownsReadResponse.newBuilder();

        for(Hometown hometown : hometownList) {
            if (hometown.getCity().equalsIgnoreCase(city)) {
                System.out.println("Classmate found.");
                response.setIsSuccess(true);
                response.addHometowns(hometown);
            }
        }

        if (!response.getIsSuccess()) {
            response.setIsSuccess(false);
            response.setError("Did not find any classmate match the city.");
        }

        HometownsReadResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
    @Override
    public void read(Empty req, StreamObserver<HometownsReadResponse> responseObserver) {
        System.out.println("Received from client: request to read all hometown info.");
        HometownsReadResponse.Builder response = HometownsReadResponse.newBuilder();

        for (Hometown hometown : hometownList) {
            response.addHometowns(hometown);
        }

        HometownsReadResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}
