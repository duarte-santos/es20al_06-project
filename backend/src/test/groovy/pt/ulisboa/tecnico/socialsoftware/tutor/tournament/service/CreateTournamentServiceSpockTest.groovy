package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification


class CreateTournamentServiceSpockTest extends Specification{

    def tournamentService

    def setup(){
        tournamentService = new TournamentService()
    }

    def "a student creates a tournament"(){
        // Tournament is created
        expect: false
    }

    def "create an already existing tournament"(){
        // An exception is thrown
        expect: false
    }

    def "create a tournament with no title"(){
        // An exception is thrown
        expect: false
    }

    def "create a tournament with wrong hour format"(){
        // An exception is thrown
        expect: false
    }

    def "create a tournament with starting hour greater than finishing hour"(){
        // An exception is thrown
        expect: false
    }

    def "choose topics that do not exist"(){
        // An exception is thrown
        expect: false
    }

    def "choose number of questions smaller than 1"(){
        // An exception is thrown
        expect: false
    }


}