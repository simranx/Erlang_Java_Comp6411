import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

public class money {
	
	int runningCustomers = 0;
	int runningBanks = 0;
	ThreadLog logobj;
	Random r = new Random(System.currentTimeMillis());
	LinkedList<ThreadBank> mainbanks = new LinkedList<money.ThreadBank>();
	
	public synchronized ThreadBank getBank(String bankName) {
		for(int i = 0; i < mainbanks.size(); i++) {
			if(mainbanks.get(i).name.equals(bankName)) {
				return mainbanks.get(i);
			}
		}
		return null;
	}
	
	class ThreadBank extends Thread {
		String name;
		int current;
		HashMap<String, ThreadCust> messages = new HashMap<String, money.ThreadCust>();
		
		public synchronized Entry<String, ThreadCust> getMessage() {
			if(this.messages.size() > 0) {
				Entry<String, ThreadCust> obj = messages.entrySet().iterator().next();
				messages.remove(obj.getKey());
				return obj;
			}
			return null;
		}
		
		public synchronized void addMessage(String msg, ThreadCust ccust) {
			this.messages.put(msg, ccust);
		}
		
		public ThreadBank(String[] info) {
			name = info[0];
			current = Integer.parseInt(info[1]);
		}
		
		public void run() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			addRunningBank();
			while(runningCustomers > 0) {
				Entry<String, ThreadCust> msg = this.getMessage();
				if(msg != null) {
					String msgInfo = msg.getKey();
					ThreadCust cust = msg.getValue();
					Integer tmp = Integer.parseInt(msgInfo.split("#")[1]);
					if(tmp <= current) {
						current = current - tmp;
						cust.setResp(1);
						logobj.addLog(name + " approves a loan of " + tmp + " dollars from " + cust.name);
					}
					else {
						cust.setResp(-1);
						logobj.addLog(name + " denies a loan of " + tmp + " dollars from " + cust.name);
					}
				}
			}
			
			logobj.addLog(name + " has " + current + " dollar(s) remaining.");
			removeRunningBank();
		}
	}

	class ThreadCust extends Thread {
		String name;
		private int total;
		private int current;
		private LinkedList<String> banks = new LinkedList<String>();
		private int resp = 0;
		
		public synchronized void setResp(int resp) {
			this.resp = resp;
		}
		
		public synchronized int getResp() {
			return this.resp;
		}
		
		public ThreadCust(String[] info, LinkedList<String> banks) {
			for(int i = 0; i < banks.size(); i++) {
				this.banks.add(banks.get(i));
			}
			name = info[0];
			total = Integer.parseInt(info[1]);
			current = Integer.parseInt(info[1]);
		}
		
		public void run() {
			addRunningCustomer();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while(true) {
				try {
					Thread.sleep(100);
					if(current == 0) {
						logobj.addLog(name + " has reached the objective of " + total + " dollar(s). Woo Hoo!");
						break;
					}
					else if(this.banks.size() == 0) {
						int bo = total - current;
						logobj.addLog(name + " was only able to borrow " + bo + " dollar(s). Boo Hoo!");
						break;
					}
					else {
						int sleepTime = r.nextInt(90) + 10;
						Thread.sleep(sleepTime);
						
						int request = r.nextInt(50) + 1;
						if(request > current) {
							request = current;
						}
						
						int choice = 0;
						if(this.banks.size() > 1) {
							choice = r.nextInt(this.banks.size());
						}
						ThreadBank tmpBank = getBank(this.banks.get(choice));
						if(tmpBank == null) {
							continue;
						}
						
						String msg = name + "#" + request;
						tmpBank.addMessage(msg, this);
						logobj.addLog(name + " requests a loan of " + request + " dollar(s) from " + this.banks.get(choice));
						
						int tmpResp = 0;
						while(tmpResp == 0) {
							tmpResp = getResp();
							Thread.sleep(40);
						}
						setResp(0);
						if(tmpResp == 1) {
							current = current - request;
						}
						else if(tmpResp == -1) {
							this.banks.remove(choice);
						}
					}
				}
				catch(Exception e) {
					System.out.println(e);
				}
			}
			removeRunningCustomer();
		}
	}

	class ThreadLog extends Thread {
		private LinkedList<String> logs = new LinkedList<String>();
		
		public synchronized void addLog(String log) {
			this.logs.add(log);
		}
		
		public synchronized String getLog() {
			if(this.logs.size() > 0) {
				String rtlog = this.logs.get(0);
				this.logs.remove(0);
				return rtlog;
			}
			return null;
		}
		
		public synchronized int getLogSize() {
			return this.logs.size();
		}
		
		public void run() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while(getRunningBanks() > 0 || getLogSize() > 0) {
				String log = getLog();
				if(log != null) {
					System.out.println(log);
				}
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized int getRunningBanks() {
		return this.runningBanks;
	}
	
	public synchronized void addRunningBank() {
		this.runningBanks++;
	}
	
	public synchronized void removeRunningBank() {
		this.runningBanks--;
	}
	
	public synchronized int getRunningCustomers() {
		return this.runningCustomers;
	}
	
	public synchronized void addRunningCustomer() {
		this.runningCustomers++;
	}
	
	public synchronized void removeRunningCustomer() {
		this.runningCustomers--;
	}

	public void startEverythign() {
		try {
			BufferedReader tmpbr = new BufferedReader(new FileReader(new File("banks.txt")));
			BufferedReader br = new BufferedReader(new FileReader(new File("customers.txt")));
			String line = null;
			LinkedList<String> inBanks = new LinkedList<String>();
			
			while ((line = tmpbr.readLine()) != null) {
				String[] data = line.substring(1,line.length() - 2).split(",");
				inBanks.add(data[0]);
			}
			
			System.out.println();
			System.out.println("** Customers and loan objectives **");
			System.out.println();
			while ((line = br.readLine()) != null) {
				String[] data = line.substring(1,line.length() - 2).split(",");
				System.out.println(data[0] + ": " + data[1]);
				new ThreadCust(data, inBanks).start();
			}
			
			System.out.println();
			System.out.println("** Banks and financial resources **");
			System.out.println();
			tmpbr = new BufferedReader(new FileReader(new File("banks.txt")));
			while ((line = tmpbr.readLine()) != null) {
				String[] data = line.substring(1,line.length() - 2).split(",");
				System.out.println(data[0] + ": " + data[1]);
				ThreadBank tb = new ThreadBank(data);
				tb.start();
				mainbanks.add(tb);
			}
			
			logobj = new ThreadLog();
			logobj.start();
		}
		catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		money obj = new money();
		obj.startEverythign();
	}

}
