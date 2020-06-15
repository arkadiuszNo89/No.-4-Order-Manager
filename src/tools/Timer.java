package tools;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;


public class Timer 
{
	private LinkedList <Timer.TimeEvent> timeEvents;
	private Timeline timeline;
	
	private LocalDate date;
	private LocalTime time;		
	private String dateString;
	private String timeString;
	
	public interface TimeEvent 
	{
		public void makeItDone(Timer timer); 
	}
	
	public Timer(int period)
	{
		timeEvents = new LinkedList <TimeEvent> ();
		
		timeline = new Timeline(
			    new KeyFrame(Duration.millis(period), (ActionEvent e) ->
			    {
					date = LocalDate.now();
					time = LocalTime.now();		
					dateString = date.format(DateTimeFormatter.ofPattern("dd LLLL yyyy"));
					timeString = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
			    	for(TimeEvent item : timeEvents) item.makeItDone(this);
			    }));
		timeline.setCycleCount(Animation.INDEFINITE);
	}
	
//	# 1
	
	public void onExit()
	{
		timeline.stop();
	}
	
	public String getDate()
	{
		return dateString;
	}
	
	public String getTime()
	{
		return timeString;
	}
	
//	# 2
	
	public void play (boolean playState)
	{
		if (playState) timeline.play();
		else timeline.stop();
	}
	
	public void addTimeEvent(TimeEvent... events)
	{
		for (TimeEvent event : events) timeEvents.add(event);
	}
	
	public static int minutesLongCounter(LocalDate date,  LocalTime time)
	{	
		int days = 0;
		
		if (date.getYear() == LocalDate.now().getYear()) 
			days = date.getDayOfYear() - LocalDate.now().getDayOfYear();
		else
		{
			days += LocalDate.now().lengthOfYear() - LocalDate.now().getDayOfYear();
			for (int x = 1; x < date.getYear() - LocalDate.now().getYear(); x++)
			{
				days += LocalDate.now().plusYears(x).lengthOfYear();
			}		
			days += date.getDayOfYear();
		}		
		
		int x = LocalTime.now().getHour()*60 + LocalTime.now().getMinute();
		int minutesCycledPerDays = (days * 60 * 24) - x;
		
		return minutesCycledPerDays + (time.getHour() *60) + time.getMinute();
	}
	
	public String timeLeft (LocalDate date, LocalTime time)
	{
		int minutes = minutesLongCounter (date, time);
		int hours = 0;
		int days = 0;
		
		days += minutes/ 1440;
		minutes -= days * 1440;
		hours += minutes / 60;
		minutes -= hours * 60;		
		
		return days+" d "+hours+" h "+minutes+" m";
	}
	
	public double progressCounter(int totalTime, LocalDate date, LocalTime time)
	{	
		int x = totalTime - minutesLongCounter(date, time);
		return (double) x / totalTime; 
	}
}