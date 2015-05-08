package userInterface;

import java.io.BufferedReader;

import company.taskMan.resource.user.UserPermission;

import userInterface.requests.AdvanceTimeRequest;
import userInterface.requests.ChangeUserRequest;
import userInterface.requests.CreateProjectRequest;
import userInterface.requests.CreateTaskRequest;
import userInterface.requests.ExitRequest;
import userInterface.requests.HelpRequest;
import userInterface.requests.InvalidRequest;
import userInterface.requests.PlanTaskRequest;
import userInterface.requests.Request;
import userInterface.requests.ShowProjectsRequest;
import userInterface.requests.SimulationRequest;
import userInterface.requests.UpdateTaskStatusRequest;

public class InputParser {

	private IFacade facade;
	private BufferedReader inputReader;

	public InputParser(IFacade facade, BufferedReader inputReader) {
		this.facade = facade;
		this.inputReader = inputReader;
	}

	public Request parse(String readLine) {
		String[] input = readLine.split(" ");

		// Switch on first word
		try {
			switch(input[0].toLowerCase()) {
			case "h"		: 
				return new HelpRequest(facade, inputReader);
			case "exit"		: 
				return new ExitRequest(facade, inputReader);
			case "advance"	: 
				return new AdvanceTimeRequest(facade, inputReader);
			case "update"	:
				if(input[1].toLowerCase().equals("task")) {
					if(facade.currentUserHasPermission(UserPermission.UPDATE_TASK)) {
						return new UpdateTaskStatusRequest(facade, inputReader);
					} else {
						throw new IllegalStateException("you need to be a developer to update tasks");
					}
				} else {
					throw new IllegalArgumentException("you can only update tasks");
				}
			case "create"	: 
				if(input[1].toLowerCase().equals("task")) {
					if(facade.currentUserHasPermission(UserPermission.CREATE_TASK)) {
						return new CreateTaskRequest(facade, inputReader);
					} else {
						throw new IllegalStateException("you need to be a project manager to create tasks");
					}
				}
				if(input[1].toLowerCase().equals("project")) {
					if(facade.currentUserHasPermission(UserPermission.CREATE_PROJECT)) {
						return new CreateProjectRequest(facade, inputReader);
					} else {
						throw new IllegalStateException("you need to be a project manager to create projects");
					}
				}
				else {
					throw new IllegalArgumentException("you can only create projects or tasks");
				}
			case "show"		: 
				if(facade.currentUserHasPermission(UserPermission.SHOW_OVERVIEW)) {
					return new ShowProjectsRequest(facade, inputReader);
				} else {
					throw new IllegalArgumentException("You don't have permission to access this overview");
				}
			case "simulate" :
				if(facade.currentUserHasPermission(UserPermission.SIMULATE)) {
					return new SimulationRequest(facade, inputReader);
				} else {
					throw new IllegalStateException("you need to be a project manager to start a simulation");
				}
			case "change"	:
				if(input[1].toLowerCase().equals("user")) {
					return new ChangeUserRequest(facade, inputReader);
				} else {
					throw new IllegalArgumentException("you can only change a user");
				}
			case "plan" :
				if(input[1].toLowerCase().equals("task")) {
					if(facade.currentUserHasPermission(UserPermission.PLAN_TASK)) { 
						return new PlanTaskRequest(facade, inputReader);
					} else {
						throw new IllegalStateException("you need to be a project manager to plan tasks");
					}
				} else {
					throw new IllegalArgumentException("you can only plan a task");
				}
			default	: 
				return new InvalidRequest(facade, inputReader, "command does not exist");
			}
		}catch(Exception e) {
			return new InvalidRequest(facade, inputReader, e.getMessage());
		}
	}
}

