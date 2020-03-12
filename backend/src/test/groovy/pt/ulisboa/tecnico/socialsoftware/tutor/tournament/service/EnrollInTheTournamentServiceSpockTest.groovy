package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification

class EnrollInTheTournamentServiceSpockTest extends Specification{

    def tournamentService

    def setup(){
        tournamentService = new TournamentService()
    }

    def "the tournament exists and a student enrolls in it"(){
        //student enrolls sucessfully
        expect : false
    }

    def "multiple tournaments exist and a student enrolls in them"(){
        //student enrolls sucessfully
        expect : false
    }

    def "the tournament exists and a student tries to enroll in it for the second time"(){
        //an exception is thrown
        expect : false
    }

    def "the tournament doesn't exist and a student tries to enroll in it"(){
        //an exception is thrown
        expect : false
    }
}