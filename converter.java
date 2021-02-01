package midiConverter;
import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class converter {
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static int[][] notes = new int[12][12];
	public static int[][] Composednotes = new int[12][12];
	public static int prevNote1 = 0;
	public static int prevNote2 = 0;
	public static int prevNote3 = 0;
	public static int currentNote = 0;
	public static int[] totalforEachRow = new int[12];
	public static int runningtotal=0;
	static ArrayList<Integer> musicpiece = new ArrayList<Integer>();
	static ArrayList<Integer> allsamples = new ArrayList<Integer>();
	public static int[][] notes2 = new int[144][12];
	public static int[][] notes3 = new int[1728][12];
	static ArrayList<Integer> order2musicpieces = new ArrayList<Integer>();
	static ArrayList<Integer> order3musicpieces = new ArrayList<Integer>();
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	public static void main(String[] args) throws Exception {
		//classical music
		String[] files1 = { "bach_846.mid","ai_cho_em_tinh_yeu.mid","ai_biet.mid","999_doa_hong.mid","1-2-3_ngoi_sao.mid",
				"238878.mid","bjsbmm01.mid","988-aria.mid","988-v01.mid","988-v02.mid",
				"988-v03.mid","988-v04.mid","988-v05.mid","988-v06.mid","988-v07.mid","988-v08.mid","988-v09.mid"};
		for(int i=0; i<files1.length; i++){
			Sequence sequence1 = MidiSystem.getSequence(new File(files1[i]));
			convertMidiToArray(sequence1);
		}

		//my own music
//		String[] files2 = {"golden.mp3.mid", "golden.wav.mid"};
//		for(int i=0; i<files2.length; i++){
//			Sequence sequence2 = MidiSystem.getSequence(new File(files2[i]));
//			convertMidiToArray(sequence2);
//		}

		//Schoenberg
		String[] files3 = {"s_11_1.mid", "s_11_2.mid","s_11_3.mid"};
		for(int i=0; i<files3.length; i++){
			Sequence sequence3 = MidiSystem.getSequence(new File(files3[i]));
			convertMidiToArray(sequence3);
		}

		//first order
		//composerMethod();

		//second order
		buildSecondOrderMarkov(allsamples);
		for(int i = 0; i<notes2.length; i++){
			for(int j = 0; j<notes2[i].length; j++){
				System.out.print(notes2[i][j] +"," + " ");
			}
			System.out.println("");
		}
		composerMethod2();

		//third order
//		buildThirdOrderMarkov(allsamples);
//		for(int i = 0; i<notes3.length; i++){
//			for(int j = 0; j<notes3[i].length; j++){
//				System.out.print(notes3[i][j] +"," + " ");
//			}
//			System.out.println("");
//		}
//		composerMethod3();
}
		public static void convertMidiToArray(Sequence s){
			int trackNumber = 0;
			for (Track track : s.getTracks()) {
				trackNumber++;
				System.out.println("Track " + trackNumber + ": size = " + track.size());
				for (int i=0; i < track.size(); i++) {
					MidiEvent event = track.get(i);
					//System.out.print("@" + event.getTick() + " ");
					MidiMessage message = event.getMessage();
					if (message instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) message;
						//System.out.print("Channel: " + sm.getChannel() + " ");
						if (sm.getCommand() == NOTE_ON) {
							int key = sm.getData1();
							int octave = (key / 12)-1;
							int note = key % 12;
							allsamples.add(note);
 							notes[prevNote1][note] += 1;
 							prevNote1 = note;

 							String noteName = NOTE_NAMES[note];
 							int velocity = sm.getData2();
 							System.out.println("Note on, " + noteName + octave + " key=" + key + "velocity: " + velocity);
						} else if (sm.getCommand() == NOTE_OFF) {
 							int key = sm.getData1();
 							int octave = (key / 12)-1;
 							int note = key % 12;
 							String noteName = NOTE_NAMES[note];
 							int velocity = sm.getData2();
 							System.out.println("Note off, " + noteName + octave + " key=" + key + "velocity: " + velocity);
						}
					} else {
						//System.out.println("Other message: " + message.getClass());
					}
				}
			}
		}
		public static void buildSecondOrderMarkov(ArrayList<Integer> samples){
			for (int i=2; i<samples.size(); i++){
				prevNote2 = samples.get(i-2);
				prevNote1 = samples.get(i-1);
				currentNote = samples.get(i);
				notes2[prevNote2*12+prevNote1][currentNote]+= 1;
			}
		}

		public static void buildThirdOrderMarkov(ArrayList<Integer> samples){
			for (int i=3; i<samples.size(); i++){
				prevNote3 = samples.get(i-3);
				prevNote2 = samples.get(i-2);
				prevNote1 = samples.get(i-1);
				currentNote = samples.get(i);
				notes3[prevNote3*12+prevNote2*12+prevNote1][currentNote]+= 1;
		}
	}


		public static void generateProbability(){
			for(int i = 0; i<notes.length; i++){
				for(int j = 0; j<notes[i].length; j++){
					runningtotal+=notes[i][j];
				}
				totalforEachRow[i]=runningtotal;
				runningtotal=0;
			}
		}

		//first order
		public static void composerMethod(){
			int currentNode =composerMethod(1);
			musicpiece.add(currentNode);
			for (int i = 0; i <100; i++){
				currentNode = composerMethod(currentNode);
				musicpiece.add(currentNode);
			}
			System.out.print(musicpiece.toString());
		}

		public static int composerMethod(int startNote){
			int largestPossbileNote = notes[startNote][0];
			int indexOfLPN = 0;
			for (int i=0; i<notes[startNote].length; i++){
				if (startNote!=i){
					if (notes[startNote][i]>largestPossbileNote){
						largestPossbileNote = notes[startNote][i];
						indexOfLPN = i;
					}}
			}
			return indexOfLPN;
		}

		//second order
		public static void composerMethod2(){
			order2musicpieces.add(1);
			order2musicpieces.add(4);
			int currentNode=0;
			for (int i = 2; i <100; i++){
				currentNode=composerMethod2(order2musicpieces.get(i-2), order2musicpieces.get(i-1));
				order2musicpieces.add(currentNode);
			}
			System.out.print(order2musicpieces.toString());
		}
		public static int composerMethod2(int firstStartNote, int SecondStartNote){
			int largestPossbileNote = notes2[firstStartNote*12+SecondStartNote][0];
			int indexOfLPN = 0;
			for (int i=0; i<notes2[firstStartNote*12+SecondStartNote].length; i++){
				if (firstStartNote*12+SecondStartNote!=i){
					if (notes2[firstStartNote*12+SecondStartNote][i]>largestPossbileNote){
						largestPossbileNote = notes2[firstStartNote*12+SecondStartNote][i];
						indexOfLPN = i;
					}}
			}
			return indexOfLPN;
		}

		//third order
//		public static void composerMethod3(){
//			order3musicpieces.add(0);
//			order3musicpieces.add(1);
//			order3musicpieces.add(2);
//			int currentNode=0;
//			for (int i = 3; i <100; i++){
//				currentNode=composerMethod3(order3musicpieces.get(i-3),order3musicpieces.get(i-2), order2musicpieces.get(i-1));
//				order3musicpieces.add(currentNode);
//			}
//			System.out.print(order3musicpieces.toString());
//		}
//		public static int composerMethod3(int firstStartNote, int SecondStartNote, int lastStartNote){
//			int largestPossbileNote = notes3[firstStartNote*12+SecondStartNote+lastStartNote][0];
//			int indexOfLPN = 0;
//			for (int i=0; i<notes3[firstStartNote*12+SecondStartNote+lastStartNote].length; i++){
//			if (firstStartNote*12+SecondStartNote+lastStartNote!=i){
//				if (notes3[firstStartNote*12+SecondStartNote+lastStartNote][i]>largestPossbileNote){
//					largestPossbileNote = notes2[firstStartNote*12+SecondStartNote+lastStartNote][i];
//					indexOfLPN = i;
//				}}
//			}
//			return indexOfLPN;
//		}

	}

