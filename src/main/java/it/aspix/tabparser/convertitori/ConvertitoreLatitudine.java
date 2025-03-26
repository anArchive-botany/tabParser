package it.aspix.tabparser.convertitori;

public class ConvertitoreLatitudine extends ConvertitoreCoordinata {
	public ConvertitoreLatitudine(){
		super.init("Nord","Sud","NORD|nord|N|n","SUD|sud|S|s");
	}
}
