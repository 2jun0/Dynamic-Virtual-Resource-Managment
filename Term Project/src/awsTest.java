import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

class InstanceStates {
	static final int PENDING = 0;
	static final int RUNNING = 16;
	static final int SHUTTING_DOWN = 32;
	static final int TERMINATED = 48;
	static final int STOPPING = 64;
	static final int STOPPED = 80;
}

public class awsTest {
	/*
	* Cloud Computing, Data Computing Laboratory
	* Department of Computer Science
	* Chungbuk National University
	*/
	static AmazonEC2 ec2;
	private static void init() throws Exception {
		/*
		* The ProfileCredentialsProvider will return your [default]
		* credential profile by reading from the credentials file located at
		* (~/.aws/credentials).
		*/
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
			"Cannot load the credentials from the credential profiles file. " +
			"Please make sure that your credentials file is at the correct " +
			"location (~/.aws/credentials), and is in valid format.",
			e);
			
		}
		ec2 = AmazonEC2ClientBuilder.standard()
		.withCredentials(credentialsProvider)
		.withRegion("us-east-1") /* check the region at AWS console */
		.build();
	}
	
	public static void main(String[] args) throws Exception {
		init();
		Scanner scanMenu = new Scanner(System.in);
		Scanner scanId = new Scanner(System.in);
		
		String instanceId;
		String imgId;
		int number = 0;
		
		while(true)
		{
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" Amazon AWS Control Panel using SDK ");
			System.out.println(" ");
			System.out.println(" Cloud Computing, Computer Science Department ");
			System.out.println(" at Chungbuk National University ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" 1. list instance\t2. available zones ");
			System.out.println(" 3. start instance\t4. available regions ");
			System.out.println(" 5. stop instance\t6. create instance ");
			System.out.println(" 7. reboot instance\t8. list images ");
			System.out.println(" 9. start all instance\t10. stop all instance ");
			System.out.println(" 11. terminate instance");
			System.out.println(" 99. quit ");
			System.out.println("------------------------------------------------------------");
			System.out.print("Enter an integer: ");
			
			number = scanMenu.nextInt();
			
			switch(number) {
			case 1:
				listInstances();
				break;
			case 2:
				availableZones();
				break;
			case 3:
				System.out.println("enter the instance id : ");
				
				instanceId = scanId.next();
				startInstance(instanceId);
				break;
			case 4:
				availableRegions();
				break;
			case 5:
				System.out.println("enter the instance id : ");
				
				instanceId = scanId.next();
				stopInstance(instanceId);
				break;
			case 6:
				System.out.println("enter the image id : ");
				
				imgId = scanId.next();
				createInstance(imgId);
				break;
			case 7:
				System.out.println("enter the instance id : ");
				
				instanceId = scanId.next();
				rebootInstance(instanceId);
				break;
			case 8:
				listImages();
				break;
			case 9:
				startAllInstances();
				break;
			case 10:
				stopAllInstances();
				break;
			case 11:
				System.out.println("enter the instance id : ");
				
				instanceId = scanId.next();
				terminateInstance(instanceId);
				break;
			case 99:
				System.exit(0);
				break;
			}
			
			System.out.println("Enter any key...");
			scanId.next();
		}
	}
	
	public static void listInstances()
	{
		System.out.println("Listing instances....");
		boolean done = false;
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					System.out.printf(
					"[id] %s, " +
					"[AMI] %s, " +
					"[type] %s, " +
					"[state] %10s, " +
					"[monitoring state] %s",
					instance.getInstanceId(),
					instance.getImageId(),
					instance.getInstanceType(),
					instance.getState().getName(),
					instance.getMonitoring().getState());
				}
				System.out.println();
			}
			request.setNextToken(response.getNextToken());
			if(response.getNextToken() == null) {
				done = true;
			}
		}
	}
	
	public static void availableZones() {
		System.out.println("Available zones....");
		
		DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();

		for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
		    System.out.printf(
		        "Found availability zone %s " +
		        "with status %s " +
		        "in region %s",
		        zone.getZoneName(),
		        zone.getState(),
		        zone.getRegionName());
		    System.out.println();
		}
	}
	
	public static void startInstance(String id) {
		System.out.println("start intance... [id:"+id+"]");
	
		StartInstancesRequest request = new StartInstancesRequest()
			.withInstanceIds(id);
		
		ec2.startInstances(request);
	}
	
	public static void startAllInstances() {
		System.out.println("start all instances....");
		boolean done = false;
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					
					if(instance.getState().getCode() == InstanceStates.STOPPED) {
						startInstance(instance.getInstanceId());
					}
				}
			}
			request.setNextToken(response.getNextToken());
			if(response.getNextToken() == null) {
				done = true;
			}
		}
	}
	
	public static void stopAllInstances() {
		System.out.println("stop all instances....");
		boolean done = false;
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					
					if(instance.getState().getCode() == InstanceStates.RUNNING) {
						stopInstance(instance.getInstanceId());
					}
				}
			}
			request.setNextToken(response.getNextToken());
			if(response.getNextToken() == null) {
				done = true;
			}
		}
	}

	public static void availableRegions() {
		System.out.println("Available regions....");
		
		DescribeRegionsResult regions_response = ec2.describeRegions();

		for(Region region : regions_response.getRegions()) {
		    System.out.printf(
	    		"Found region %s, with endpoint %s", 
	    		region.getRegionName(), region.getEndpoint()
    		);
		    System.out.println();
		}
	}
	
	public static void stopInstance(String id) {
		System.out.println("stop instance... [id:"+id+"]");
		
		StopInstancesRequest request = new StopInstancesRequest()
			.withInstanceIds(id);
		
		ec2.stopInstances(request);
	}
	
	public static void rebootInstance(String id) {
		System.out.println("reboot instance... [id:"+id+"]");
		
		RebootInstancesRequest request = new RebootInstancesRequest()
			.withInstanceIds(id);
		
		RebootInstancesResult response = ec2.rebootInstances(request);
	}
	
	public static void createInstance(String imgId) {
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		runInstancesRequest.withImageId(imgId)
			.withInstanceType(InstanceType.T2Micro)
			.withMinCount(1)
			.withMaxCount(1)
			.withKeyName("ejun0")
			.withSecurityGroups("launch-wizard-2");
		RunInstancesResult result = ec2.runInstances(runInstancesRequest);
	}
	
	public static void listImages() {
		DescribeImagesRequest request = new DescribeImagesRequest();
		DescribeImagesResult imageResult = ec2.describeImages(request);
		
		for(Image image : imageResult.getImages()) {
			System.out.printf(
				"[name] %s, " + 
				"[id] %s, " +
				"[type] %s, " +
				"[state] %10s, ",
				image.getName(),
				image.getImageId(),
				image.getImageType(),
				image.getState()
			);
			System.out.println();
		}
	}
	
	public static void terminateInstance(String id) {
		System.out.println("terminate instance... [id:"+id+"]");
		
		TerminateInstancesRequest request = new TerminateInstancesRequest()
				.withInstanceIds(id);
		
		ec2.terminateInstances(request);
	}
}