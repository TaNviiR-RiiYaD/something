package com.chess.spring.game;

import com.chess.spring.exceptions.ExceptionMessages;
import com.chess.spring.exceptions.InvalidDataException;
import com.chess.spring.game.board.Board;
import com.chess.spring.game.board.BoardBuilder;
import com.chess.spring.game.board.BoardService;
import com.chess.spring.game.core.analysers.BoardConfiguration;
import com.chess.spring.game.pieces.*;
import com.chess.spring.game.pieces.utils.PlayerColor;
import com.google.common.primitives.Chars;

import java.util.LinkedList;
import java.util.List;

public class FenService {
    public static String parse(Board board) {
        return calculateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculatePassingField(board) + " " +
                "0 1";
    }

    public static Board parse(String fen) throws InvalidDataException {
        String[] fenParts = fen.trim().split(" ");
        BoardBuilder builder = new BoardBuilder();
        boolean whiteKingSideCastle = whiteKingSideCastle(fenParts[2]);
        boolean whiteQueenSideCastle = whiteQueenSideCastle(fenParts[2]);
        boolean blackKingSideCastle = blackKingSideCastle(fenParts[2]);
        boolean blackQueenSideCastle = blackQueenSideCastle(fenParts[2]);
        String gameConfiguration = fenParts[0];
        List<Character> boardTiles = new LinkedList<>(Chars.asList(gameConfiguration
                .replaceAll("/", "")
                .replaceAll("8", "--------")
                .replaceAll("7", "-------")
                .replaceAll("6", "------")
                .replaceAll("5", "-----")
                .replaceAll("4", "----")
                .replaceAll("3", "---")
                .replaceAll("2", "--")
                .replaceAll("1", "-")
                .toCharArray()));
        for (int i = 0; i < boardTiles.size(); i++) {
            switch (boardTiles.get(i)) {
                case 'r':
                    builder.setPiece(new Rook(PlayerColor.BLACK, i));
                    continue;
                case 'n':
                    builder.setPiece(new Knight(PlayerColor.BLACK, i));
                    continue;
                case 'b':
                    builder.setPiece(new Bishop(PlayerColor.BLACK, i));
                    continue;
                case 'q':
                    builder.setPiece(new Queen(PlayerColor.BLACK, i));
                    continue;
                case 'k':
                    builder.setPiece(new King(PlayerColor.BLACK, i, blackKingSideCastle, blackQueenSideCastle));
                    continue;
                case 'p':
                    builder.setPiece(new Pawn(PlayerColor.BLACK, i));
                    continue;
                case 'R':
                    builder.setPiece(new Rook(PlayerColor.WHITE, i));
                    continue;
                case 'N':
                    builder.setPiece(new Knight(PlayerColor.WHITE, i));
                    continue;
                case 'B':
                    builder.setPiece(new Bishop(PlayerColor.WHITE, i));
                    continue;
                case 'Q':
                    builder.setPiece(new Queen(PlayerColor.WHITE, i));
                    continue;
                case 'K':
                    builder.setPiece(new King(PlayerColor.WHITE, i, whiteKingSideCastle, whiteQueenSideCastle));
                    continue;
                case 'P':
                    builder.setPiece(new Pawn(PlayerColor.WHITE, i));
                    continue;
                case '-':
                    continue;
                default:
                    throw new RuntimeException("Invalid FEN String " + gameConfiguration);
            }
        }
        builder.setMoveMaker(moveMaker(fenParts[1]));
        return builder.build();
    }

    private static PlayerColor moveMaker(String player) throws InvalidDataException {
        if (player.equals("w")) {
            return PlayerColor.WHITE;
        }
        if (player.equals("b")) {
            return PlayerColor.BLACK;
        }
        throw new InvalidDataException(ExceptionMessages.SYSTEM_ERROR_INVALID_DATA.getInfo());
    }

    private static boolean whiteKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("K");
    }

    private static boolean whiteQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("Q");
    }

    private static boolean blackKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("k");
    }

    private static boolean blackQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("q");
    }

    private static String calculateCastleText(Board board) {
        StringBuilder builder = new StringBuilder();
        if (board.whitePlayer().isKingSideCastleCapable()) {
            builder.append("K");
        }
        if (board.whitePlayer().isQueenSideCastleCapable()) {
            builder.append("Q");
        }
        if (board.blackPlayer().isKingSideCastleCapable()) {
            builder.append("k");
        }
        if (board.blackPlayer().isQueenSideCastleCapable()) {
            builder.append("q");
        }
        String result = builder.toString();

        return result.isEmpty() ? "-" : result;
    }

    private static String calculatePassingField(Board board) {
        Pawn enPassantPawn = board.getPassingAttack();
        if (enPassantPawn != null) {
            return BoardService.getPositionAtCoordinate(enPassantPawn.getPosition() +
                    (8) * enPassantPawn.getPieceAllegiance().getOppositeDirection());
        }
        return "-";
    }

    private static String calculateBoardText(Board board) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardConfiguration.TILES_MAX; i++) {
            String tileText = board.getPiece(i) == null ? "-" :
                    board.getPiece(i).getPieceAllegiance().isWhite() ? board.getPiece(i).toString() :
                            board.getPiece(i).toString().toLowerCase();
            builder.append(tileText);
        }
        builder.insert(8, "/");
        builder.insert(17, "/");
        builder.insert(26, "/");
        builder.insert(35, "/");
        builder.insert(44, "/");
        builder.insert(53, "/");
        builder.insert(62, "/");
        return builder.toString()
                .replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    private static String calculateCurrentPlayerText(Board board) {
        return board.getCurrentPlayer().toString().substring(0, 1).toLowerCase();
    }


}


package com.chess.spring.game;

import com.chess.spring.exceptions.ExceptionMessages;
import com.chess.spring.exceptions.InvalidDataException;
import com.chess.spring.game.board.Board;
import com.chess.spring.game.board.BoardBuilder;
import com.chess.spring.game.board.BoardService;
import com.chess.spring.game.core.analysers.BoardConfiguration;
import com.chess.spring.game.pieces.*;
import com.chess.spring.game.pieces.utils.PlayerColor;
import com.google.common.primitives.Chars;

import java.util.LinkedList;
import java.util.List;

public class FenService {
    public static String parse(Board board) {
        return calculateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculatePassingField(board) + " " +
                "0 1";
    }

    public static Board parse(String fen) throws InvalidDataException {
        String[] fenParts = fen.trim().split(" ");
        BoardBuilder builder = new BoardBuilder();
        boolean whiteKingSideCastle = whiteKingSideCastle(fenParts[2]);
        boolean whiteQueenSideCastle = whiteQueenSideCastle(fenParts[2]);
        boolean blackKingSideCastle = blackKingSideCastle(fenParts[2]);
        boolean blackQueenSideCastle = blackQueenSideCastle(fenParts[2]);
        String gameConfiguration = fenParts[0];
        List<Character> boardTiles = new LinkedList<>(Chars.asList(gameConfiguration
                .replaceAll("/", "")
                .replaceAll("8", "--------")
                .replaceAll("7", "-------")
                .replaceAll("6", "------")
                .replaceAll("5", "-----")
                .replaceAll("4", "----")
                .replaceAll("3", "---")
                .replaceAll("2", "--")
                .replaceAll("1", "-")
                .toCharArray()));
        for (int i = 0; i < boardTiles.size(); i++) {
            switch (boardTiles.get(i)) {
                case 'r':
                    builder.setPiece(new Rook(PlayerColor.BLACK, i));
                    continue;
                case 'n':
                    builder.setPiece(new Knight(PlayerColor.BLACK, i));
                    continue;
                case 'b':
                    builder.setPiece(new Bishop(PlayerColor.BLACK, i));
                    continue;
                case 'q':
                    builder.setPiece(new Queen(PlayerColor.BLACK, i));
                    continue;
                case 'k':
                    builder.setPiece(new King(PlayerColor.BLACK, i, blackKingSideCastle, blackQueenSideCastle));
                    continue;
                case 'p':
                    builder.setPiece(new Pawn(PlayerColor.BLACK, i));
                    continue;
                case 'R':
                    builder.setPiece(new Rook(PlayerColor.WHITE, i));
                    continue;
                case 'N':
                    builder.setPiece(new Knight(PlayerColor.WHITE, i));
                    continue;
                case 'B':
                    builder.setPiece(new Bishop(PlayerColor.WHITE, i));
                    continue;
                case 'Q':
                    builder.setPiece(new Queen(PlayerColor.WHITE, i));
                    continue;
                case 'K':
                    builder.setPiece(new King(PlayerColor.WHITE, i, whiteKingSideCastle, whiteQueenSideCastle));
                    continue;
                case 'P':
                    builder.setPiece(new Pawn(PlayerColor.WHITE, i));
                    continue;
                case '-':
                    continue;
                default:
                    throw new RuntimeException("Invalid FEN String " + gameConfiguration);
            }
        }
        builder.setMoveMaker(moveMaker(fenParts[1]));
        return builder.build();
    }

    private static PlayerColor moveMaker(String player) throws InvalidDataException {
        if (player.equals("w")) {
            return PlayerColor.WHITE;
        }
        if (player.equals("b")) {
            return PlayerColor.BLACK;
        }
        throw new InvalidDataException(ExceptionMessages.SYSTEM_ERROR_INVALID_DATA.getInfo());
    }

    private static boolean whiteKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("K");
    }

    private static boolean whiteQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("Q");
    }

    private static boolean blackKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("k");
    }

    private static boolean blackQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("q");
    }

    private static String calculateCastleText(Board board) {
        StringBuilder builder = new StringBuilder();
        if (board.whitePlayer().isKingSideCastleCapable()) {
            builder.append("K");
        }
        if (board.whitePlayer().isQueenSideCastleCapable()) {
            builder.append("Q");
        }
        if (board.blackPlayer().isKingSideCastleCapable()) {
            builder.append("k");
        }
        if (board.blackPlayer().isQueenSideCastleCapable()) {
            builder.append("q");
        }
        String result = builder.toString();

        return result.isEmpty() ? "-" : result;
    }

    private static String calculatePassingField(Board board) {
        Pawn enPassantPawn = board.getPassingAttack();
        if (enPassantPawn != null) {
            return BoardService.getPositionAtCoordinate(enPassantPawn.getPosition() +
                    (8) * enPassantPawn.getPieceAllegiance().getOppositeDirection());
        }
        return "-";
    }

    private static String calculateBoardText(Board board) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardConfiguration.TILES_MAX; i++) {
            String tileText = board.getPiece(i) == null ? "-" :
                    board.getPiece(i).getPieceAllegiance().isWhite() ? board.getPiece(i).toString() :
                            board.getPiece(i).toString().toLowerCase();
            builder.append(tileText);
        }
        builder.insert(8, "/");
        builder.insert(17, "/");
        builder.insert(26, "/");
        builder.insert(35, "/");
        builder.insert(44, "/");
        builder.insert(53, "/");
        builder.insert(62, "/");
        return builder.toString()
                .replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    private static String calculateCurrentPlayerText(Board board) {
        return board.getCurrentPlayer().toString().substring(0, 1).toLowerCase();
    }


}
package com.chess.spring.game;

import com.chess.spring.exceptions.ExceptionMessages;
import com.chess.spring.exceptions.InvalidDataException;
import com.chess.spring.game.board.Board;
import com.chess.spring.game.board.BoardBuilder;
import com.chess.spring.game.board.BoardService;
import com.chess.spring.game.core.analysers.BoardConfiguration;
import com.chess.spring.game.pieces.*;
import com.chess.spring.game.pieces.utils.PlayerColor;
import com.google.common.primitives.Chars;

import java.util.LinkedList;
import java.util.List;

public class FenService {
    public static String parse(Board board) {
        return calculateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculatePassingField(board) + " " +
                "0 1";
    }

    public static Board parse(String fen) throws InvalidDataException {
        String[] fenParts = fen.trim().split(" ");
        BoardBuilder builder = new BoardBuilder();
        boolean whiteKingSideCastle = whiteKingSideCastle(fenParts[2]);
        boolean whiteQueenSideCastle = whiteQueenSideCastle(fenParts[2]);
        boolean blackKingSideCastle = blackKingSideCastle(fenParts[2]);
        boolean blackQueenSideCastle = blackQueenSideCastle(fenParts[2]);
        String gameConfiguration = fenParts[0];
        List<Character> boardTiles = new LinkedList<>(Chars.asList(gameConfiguration
                .replaceAll("/", "")
                .replaceAll("8", "--------")
                .replaceAll("7", "-------")
                .replaceAll("6", "------")
                .replaceAll("5", "-----")
                .replaceAll("4", "----")
                .replaceAll("3", "---")
                .replaceAll("2", "--")
                .replaceAll("1", "-")
                .toCharArray()));
        for (int i = 0; i < boardTiles.size(); i++) {
            switch (boardTiles.get(i)) {
                case 'r':
                    builder.setPiece(new Rook(PlayerColor.BLACK, i));
                    continue;
                case 'n':
                    builder.setPiece(new Knight(PlayerColor.BLACK, i));
                    continue;
                case 'b':
                    builder.setPiece(new Bishop(PlayerColor.BLACK, i));
                    continue;
                case 'q':
                    builder.setPiece(new Queen(PlayerColor.BLACK, i));
                    continue;
                case 'k':
                    builder.setPiece(new King(PlayerColor.BLACK, i, blackKingSideCastle, blackQueenSideCastle));
                    continue;
                case 'p':
                    builder.setPiece(new Pawn(PlayerColor.BLACK, i));
                    continue;
                case 'R':
                    builder.setPiece(new Rook(PlayerColor.WHITE, i));
                    continue;
                case 'N':
                    builder.setPiece(new Knight(PlayerColor.WHITE, i));
                    continue;
                case 'B':
                    builder.setPiece(new Bishop(PlayerColor.WHITE, i));
                    continue;
                case 'Q':
                    builder.setPiece(new Queen(PlayerColor.WHITE, i));
                    continue;
                case 'K':
                    builder.setPiece(new King(PlayerColor.WHITE, i, whiteKingSideCastle, whiteQueenSideCastle));
                    continue;
                case 'P':
                    builder.setPiece(new Pawn(PlayerColor.WHITE, i));
                    continue;
                case '-':
                    continue;
                default:
                    throw new RuntimeException("Invalid FEN String " + gameConfiguration);
            }
        }
        builder.setMoveMaker(moveMaker(fenParts[1]));
        return builder.build();
    }

    private static PlayerColor moveMaker(String player) throws InvalidDataException {
        if (player.equals("w")) {
            return PlayerColor.WHITE;
        }
        if (player.equals("b")) {
            return PlayerColor.BLACK;
        }
        throw new InvalidDataException(ExceptionMessages.SYSTEM_ERROR_INVALID_DATA.getInfo());
    }

    private static boolean whiteKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("K");
    }

    private static boolean whiteQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("Q");
    }

    private static boolean blackKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("k");
    }

    private static boolean blackQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("q");
    }

    private static String calculateCastleText(Board board) {
        StringBuilder builder = new StringBuilder();
        if (board.whitePlayer().isKingSideCastleCapable()) {
            builder.append("K");
        }
        if (board.whitePlayer().isQueenSideCastleCapable()) {
            builder.append("Q");
        }
        if (board.blackPlayer().isKingSideCastleCapable()) {
            builder.append("k");
        }
        if (board.blackPlayer().isQueenSideCastleCapable()) {
            builder.append("q");
        }
        String result = builder.toString();

        return result.isEmpty() ? "-" : result;
    }

    private static String calculatePassingField(Board board) {
        Pawn enPassantPawn = board.getPassingAttack();
        if (enPassantPawn != null) {
            return BoardService.getPositionAtCoordinate(enPassantPawn.getPosition() +
                    (8) * enPassantPawn.getPieceAllegiance().getOppositeDirection());
        }
        return "-";
    }

    private static String calculateBoardText(Board board) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardConfiguration.TILES_MAX; i++) {
            String tileText = board.getPiece(i) == null ? "-" :
                    board.getPiece(i).getPieceAllegiance().isWhite() ? board.getPiece(i).toString() :
                            board.getPiece(i).toString().toLowerCase();
            builder.append(tileText);
        }
        builder.insert(8, "/");
        builder.insert(17, "/");
        builder.insert(26, "/");
        builder.insert(35, "/");
        builder.insert(44, "/");
        builder.insert(53, "/");
        builder.insert(62, "/");
        return builder.toString()
                .replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    private static String calculateCurrentPlayerText(Board board) {
        return board.getCurrentPlayer().toString().substring(0, 1).toLowerCase();
    }


}



package com.chess.spring.game;

import com.chess.spring.exceptions.ExceptionMessages;
import com.chess.spring.exceptions.InvalidDataException;
import com.chess.spring.game.board.Board;
import com.chess.spring.game.board.BoardBuilder;
import com.chess.spring.game.board.BoardService;
import com.chess.spring.game.core.analysers.BoardConfiguration;
import com.chess.spring.game.pieces.*;
import com.chess.spring.game.pieces.utils.PlayerColor;
import com.google.common.primitives.Chars;

import java.util.LinkedList;
import java.util.List;

public class FenService {
    public static String parse(Board board) {
        return calculateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculatePassingField(board) + " " +
                "0 1";
    }

    public static Board parse(String fen) throws InvalidDataException {
        String[] fenParts = fen.trim().split(" ");
        BoardBuilder builder = new BoardBuilder();
        boolean whiteKingSideCastle = whiteKingSideCastle(fenParts[2]);
        boolean whiteQueenSideCastle = whiteQueenSideCastle(fenParts[2]);
        boolean blackKingSideCastle = blackKingSideCastle(fenParts[2]);
        boolean blackQueenSideCastle = blackQueenSideCastle(fenParts[2]);
        String gameConfiguration = fenParts[0];
        List<Character> boardTiles = new LinkedList<>(Chars.asList(gameConfiguration
                .replaceAll("/", "")
                .replaceAll("8", "--------")
                .replaceAll("7", "-------")
                .replaceAll("6", "------")
                .replaceAll("5", "-----")
                .replaceAll("4", "----")
                .replaceAll("3", "---")
                .replaceAll("2", "--")
                .replaceAll("1", "-")
                .toCharArray()));
        for (int i = 0; i < boardTiles.size(); i++) {
            switch (boardTiles.get(i)) {
                case 'r':
                    builder.setPiece(new Rook(PlayerColor.BLACK, i));
                    continue;
                case 'n':
                    builder.setPiece(new Knight(PlayerColor.BLACK, i));
                    continue;
                case 'b':
                    builder.setPiece(new Bishop(PlayerColor.BLACK, i));
                    continue;
                case 'q':
                    builder.setPiece(new Queen(PlayerColor.BLACK, i));
                    continue;
                case 'k':
                    builder.setPiece(new King(PlayerColor.BLACK, i, blackKingSideCastle, blackQueenSideCastle));
                    continue;
                case 'p':
                    builder.setPiece(new Pawn(PlayerColor.BLACK, i));
                    continue;
                case 'R':
                    builder.setPiece(new Rook(PlayerColor.WHITE, i));
                    continue;
                case 'N':
                    builder.setPiece(new Knight(PlayerColor.WHITE, i));
                    continue;
                case 'B':
                    builder.setPiece(new Bishop(PlayerColor.WHITE, i));
                    continue;
                case 'Q':
                    builder.setPiece(new Queen(PlayerColor.WHITE, i));
                    continue;
                case 'K':
                    builder.setPiece(new King(PlayerColor.WHITE, i, whiteKingSideCastle, whiteQueenSideCastle));
                    continue;
                case 'P':
                    builder.setPiece(new Pawn(PlayerColor.WHITE, i));
                    continue;
                case '-':
                    continue;
                default:
                    throw new RuntimeException("Invalid FEN String " + gameConfiguration);
            }
        }
        builder.setMoveMaker(moveMaker(fenParts[1]));
        return builder.build();
    }

    private static PlayerColor moveMaker(String player) throws InvalidDataException {
        if (player.equals("w")) {
            return PlayerColor.WHITE;
        }
        if (player.equals("b")) {
            return PlayerColor.BLACK;
        }
        throw new InvalidDataException(ExceptionMessages.SYSTEM_ERROR_INVALID_DATA.getInfo());
    }

    private static boolean whiteKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("K");
    }

    private static boolean whiteQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("Q");
    }

    private static boolean blackKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("k");
    }

    private static boolean blackQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("q");
    }

    private static String calculateCastleText(Board board) {
        StringBuilder builder = new StringBuilder();
        if (board.whitePlayer().isKingSideCastleCapable()) {
            builder.append("K");
        }
        if (board.whitePlayer().isQueenSideCastleCapable()) {
            builder.append("Q");
        }
        if (board.blackPlayer().isKingSideCastleCapable()) {
            builder.append("k");
        }
        if (board.blackPlayer().isQueenSideCastleCapable()) {
            builder.append("q");
        }
        String result = builder.toString();

        return result.isEmpty() ? "-" : result;
    }

    private static String calculatePassingField(Board board) {
        Pawn enPassantPawn = board.getPassingAttack();
        if (enPassantPawn != null) {
            return BoardService.getPositionAtCoordinate(enPassantPawn.getPosition() +
                    (8) * enPassantPawn.getPieceAllegiance().getOppositeDirection());
        }
        return "-";
    }

    private static String calculateBoardText(Board board) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardConfiguration.TILES_MAX; i++) {
            String tileText = board.getPiece(i) == null ? "-" :
                    board.getPiece(i).getPieceAllegiance().isWhite() ? board.getPiece(i).toString() :
                            board.getPiece(i).toString().toLowerCase();
            builder.append(tileText);
        }
        builder.insert(8, "/");
        builder.insert(17, "/");
        builder.insert(26, "/");
        builder.insert(35, "/");
        builder.insert(44, "/");
        builder.insert(53, "/");
        builder.insert(62, "/");
        return builder.toString()
                .replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    private static String calculateCurrentPlayerText(Board board) {
        return board.getCurrentPlayer().toString().substring(0, 1).toLowerCase();
    }


}


package com.chess.spring.game;

import com.chess.spring.exceptions.ExceptionMessages;
import com.chess.spring.exceptions.InvalidDataException;
import com.chess.spring.game.board.Board;
import com.chess.spring.game.board.BoardBuilder;
import com.chess.spring.game.board.BoardService;
import com.chess.spring.game.core.analysers.BoardConfiguration;
import com.chess.spring.game.pieces.*;
import com.chess.spring.game.pieces.utils.PlayerColor;
import com.google.common.primitives.Chars;

import java.util.LinkedList;
import java.util.List;

public class FenService {
    public static String parse(Board board) {
        return calculateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculatePassingField(board) + " " +
                "0 1";
    }

    public static Board parse(String fen) throws InvalidDataException {
        String[] fenParts = fen.trim().split(" ");
        BoardBuilder builder = new BoardBuilder();
        boolean whiteKingSideCastle = whiteKingSideCastle(fenParts[2]);
        boolean whiteQueenSideCastle = whiteQueenSideCastle(fenParts[2]);
        boolean blackKingSideCastle = blackKingSideCastle(fenParts[2]);
        boolean blackQueenSideCastle = blackQueenSideCastle(fenParts[2]);
        String gameConfiguration = fenParts[0];
        List<Character> boardTiles = new LinkedList<>(Chars.asList(gameConfiguration
                .replaceAll("/", "")
                .replaceAll("8", "--------")
                .replaceAll("7", "-------")
                .replaceAll("6", "------")
                .replaceAll("5", "-----")
                .replaceAll("4", "----")
                .replaceAll("3", "---")
                .replaceAll("2", "--")
                .replaceAll("1", "-")
                .toCharArray()));
        for (int i = 0; i < boardTiles.size(); i++) {
            switch (boardTiles.get(i)) {
                case 'r':
                    builder.setPiece(new Rook(PlayerColor.BLACK, i));
                    continue;
                case 'n':
                    builder.setPiece(new Knight(PlayerColor.BLACK, i));
                    continue;
                case 'b':
                    builder.setPiece(new Bishop(PlayerColor.BLACK, i));
                    continue;
                case 'q':
                    builder.setPiece(new Queen(PlayerColor.BLACK, i));
                    continue;
                case 'k':
                    builder.setPiece(new King(PlayerColor.BLACK, i, blackKingSideCastle, blackQueenSideCastle));
                    continue;
                case 'p':
                    builder.setPiece(new Pawn(PlayerColor.BLACK, i));
                    continue;
                case 'R':
                    builder.setPiece(new Rook(PlayerColor.WHITE, i));
                    continue;
                case 'N':
                    builder.setPiece(new Knight(PlayerColor.WHITE, i));
                    continue;
                case 'B':
                    builder.setPiece(new Bishop(PlayerColor.WHITE, i));
                    continue;
                case 'Q':
                    builder.setPiece(new Queen(PlayerColor.WHITE, i));
                    continue;
                case 'K':
                    builder.setPiece(new King(PlayerColor.WHITE, i, whiteKingSideCastle, whiteQueenSideCastle));
                    continue;
                case 'P':
                    builder.setPiece(new Pawn(PlayerColor.WHITE, i));
                    continue;
                case '-':
                    continue;
                default:
                    throw new RuntimeException("Invalid FEN String " + gameConfiguration);
            }
        }
        builder.setMoveMaker(moveMaker(fenParts[1]));
        return builder.build();
    }

    private static PlayerColor moveMaker(String player) throws InvalidDataException {
        if (player.equals("w")) {
            return PlayerColor.WHITE;
        }
        if (player.equals("b")) {
            return PlayerColor.BLACK;
        }
        throw new InvalidDataException(ExceptionMessages.SYSTEM_ERROR_INVALID_DATA.getInfo());
    }

    private static boolean whiteKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("K");
    }

    private static boolean whiteQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("Q");
    }

    private static boolean blackKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("k");
    }

    private static boolean blackQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("q");
    }

    private static String calculateCastleText(Board board) {
        StringBuilder builder = new StringBuilder();
        if (board.whitePlayer().isKingSideCastleCapable()) {
            builder.append("K");
        }
        if (board.whitePlayer().isQueenSideCastleCapable()) {
            builder.append("Q");
        }
        if (board.blackPlayer().isKingSideCastleCapable()) {
            builder.append("k");
        }
        if (board.blackPlayer().isQueenSideCastleCapable()) {
            builder.append("q");
        }
        String result = builder.toString();

        return result.isEmpty() ? "-" : result;
    }

    private static String calculatePassingField(Board board) {
        Pawn enPassantPawn = board.getPassingAttack();
        if (enPassantPawn != null) {
            return BoardService.getPositionAtCoordinate(enPassantPawn.getPosition() +
                    (8) * enPassantPawn.getPieceAllegiance().getOppositeDirection());
        }
        return "-";
    }

    private static String calculateBoardText(Board board) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardConfiguration.TILES_MAX; i++) {
            String tileText = board.getPiece(i) == null ? "-" :
                    board.getPiece(i).getPieceAllegiance().isWhite() ? board.getPiece(i).toString() :
                            board.getPiece(i).toString().toLowerCase();
            builder.append(tileText);
        }
        builder.insert(8, "/");
        builder.insert(17, "/");
        builder.insert(26, "/");
        builder.insert(35, "/");
        builder.insert(44, "/");
        builder.insert(53, "/");
        builder.insert(62, "/");
        return builder.toString()
                .replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    private static String calculateCurrentPlayerText(Board board) {
        return board.getCurrentPlayer().toString().substring(0, 1).toLowerCase();
    }


}




package com.chess.spring.game;

import com.chess.spring.exceptions.ExceptionMessages;
import com.chess.spring.exceptions.InvalidDataException;
import com.chess.spring.game.board.Board;
import com.chess.spring.game.board.BoardBuilder;
import com.chess.spring.game.board.BoardService;
import com.chess.spring.game.core.analysers.BoardConfiguration;
import com.chess.spring.game.pieces.*;
import com.chess.spring.game.pieces.utils.PlayerColor;
import com.google.common.primitives.Chars;

import java.util.LinkedList;
import java.util.List;

public class FenService {
    public static String parse(Board board) {
        return calculateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculatePassingField(board) + " " +
                "0 1";
    }

    public static Board parse(String fen) throws InvalidDataException {
        String[] fenParts = fen.trim().split(" ");
        BoardBuilder builder = new BoardBuilder();
        boolean whiteKingSideCastle = whiteKingSideCastle(fenParts[2]);
        boolean whiteQueenSideCastle = whiteQueenSideCastle(fenParts[2]);
        boolean blackKingSideCastle = blackKingSideCastle(fenParts[2]);
        boolean blackQueenSideCastle = blackQueenSideCastle(fenParts[2]);
        String gameConfiguration = fenParts[0];
        List<Character> boardTiles = new LinkedList<>(Chars.asList(gameConfiguration
                .replaceAll("/", "")
                .replaceAll("8", "--------")
                .replaceAll("7", "-------")
                .replaceAll("6", "------")
                .replaceAll("5", "-----")
                .replaceAll("4", "----")
                .replaceAll("3", "---")
                .replaceAll("2", "--")
                .replaceAll("1", "-")
                .toCharArray()));
        for (int i = 0; i < boardTiles.size(); i++) {
            switch (boardTiles.get(i)) {
                case 'r':
                    builder.setPiece(new Rook(PlayerColor.BLACK, i));
                    continue;
                case 'n':
                    builder.setPiece(new Knight(PlayerColor.BLACK, i));
                    continue;
                case 'b':
                    builder.setPiece(new Bishop(PlayerColor.BLACK, i));
                    continue;
                case 'q':
                    builder.setPiece(new Queen(PlayerColor.BLACK, i));
                    continue;
                case 'k':
                    builder.setPiece(new King(PlayerColor.BLACK, i, blackKingSideCastle, blackQueenSideCastle));
                    continue;
                case 'p':
                    builder.setPiece(new Pawn(PlayerColor.BLACK, i));
                    continue;
                case 'R':
                    builder.setPiece(new Rook(PlayerColor.WHITE, i));
                    continue;
                case 'N':
                    builder.setPiece(new Knight(PlayerColor.WHITE, i));
                    continue;
                case 'B':
                    builder.setPiece(new Bishop(PlayerColor.WHITE, i));
                    continue;
                case 'Q':
                    builder.setPiece(new Queen(PlayerColor.WHITE, i));
                    continue;
                case 'K':
                    builder.setPiece(new King(PlayerColor.WHITE, i, whiteKingSideCastle, whiteQueenSideCastle));
                    continue;
                case 'P':
                    builder.setPiece(new Pawn(PlayerColor.WHITE, i));
                    continue;
                case '-':
                    continue;
                default:
                    throw new RuntimeException("Invalid FEN String " + gameConfiguration);
            }
        }
        builder.setMoveMaker(moveMaker(fenParts[1]));
        return builder.build();
    }

    private static PlayerColor moveMaker(String player) throws InvalidDataException {
        if (player.equals("w")) {
            return PlayerColor.WHITE;
        }
        if (player.equals("b")) {
            return PlayerColor.BLACK;
        }
        throw new InvalidDataException(ExceptionMessages.SYSTEM_ERROR_INVALID_DATA.getInfo());
    }

    private static boolean whiteKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("K");
    }

    private static boolean whiteQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("Q");
    }

    private static boolean blackKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("k");
    }

    private static boolean blackQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("q");
    }

    private static String calculateCastleText(Board board) {
        StringBuilder builder = new StringBuilder();
        if (board.whitePlayer().isKingSideCastleCapable()) {
            builder.append("K");
        }
        if (board.whitePlayer().isQueenSideCastleCapable()) {
            builder.append("Q");
        }
        if (board.blackPlayer().isKingSideCastleCapable()) {
            builder.append("k");
        }
        if (board.blackPlayer().isQueenSideCastleCapable()) {
            builder.append("q");
        }
        String result = builder.toString();

        return result.isEmpty() ? "-" : result;
    }

    private static String calculatePassingField(Board board) {
        Pawn enPassantPawn = board.getPassingAttack();
        if (enPassantPawn != null) {
            return BoardService.getPositionAtCoordinate(enPassantPawn.getPosition() +
                    (8) * enPassantPawn.getPieceAllegiance().getOppositeDirection());
        }
        return "-";
    }

    private static String calculateBoardText(Board board) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardConfiguration.TILES_MAX; i++) {
            String tileText = board.getPiece(i) == null ? "-" :
                    board.getPiece(i).getPieceAllegiance().isWhite() ? board.getPiece(i).toString() :
                            board.getPiece(i).toString().toLowerCase();
            builder.append(tileText);
        }
        builder.insert(8, "/");
        builder.insert(17, "/");
        builder.insert(26, "/");
        builder.insert(35, "/");
        builder.insert(44, "/");
        builder.insert(53, "/");
        builder.insert(62, "/");
        return builder.toString()
                .replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    private static String calculateCurrentPlayerText(Board board) {
        return board.getCurrentPlayer().toString().substring(0, 1).toLowerCase();
    }


}




package com.chess.spring.game;

import com.chess.spring.exceptions.ExceptionMessages;
import com.chess.spring.exceptions.InvalidDataException;
import com.chess.spring.game.board.Board;
import com.chess.spring.game.board.BoardBuilder;
import com.chess.spring.game.board.BoardService;
import com.chess.spring.game.core.analysers.BoardConfiguration;
import com.chess.spring.game.pieces.*;
import com.chess.spring.game.pieces.utils.PlayerColor;
import com.google.common.primitives.Chars;

import java.util.LinkedList;
import java.util.List;

public class FenService {
    public static String parse(Board board) {
        return calculateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculatePassingField(board) + " " +
                "0 1";
    }

    public static Board parse(String fen) throws InvalidDataException {
        String[] fenParts = fen.trim().split(" ");
        BoardBuilder builder = new BoardBuilder();
        boolean whiteKingSideCastle = whiteKingSideCastle(fenParts[2]);
        boolean whiteQueenSideCastle = whiteQueenSideCastle(fenParts[2]);
        boolean blackKingSideCastle = blackKingSideCastle(fenParts[2]);
        boolean blackQueenSideCastle = blackQueenSideCastle(fenParts[2]);
        String gameConfiguration = fenParts[0];
        List<Character> boardTiles = new LinkedList<>(Chars.asList(gameConfiguration
                .replaceAll("/", "")
                .replaceAll("8", "--------")
                .replaceAll("7", "-------")
                .replaceAll("6", "------")
                .replaceAll("5", "-----")
                .replaceAll("4", "----")
                .replaceAll("3", "---")
                .replaceAll("2", "--")
                .replaceAll("1", "-")
                .toCharArray()));
        for (int i = 0; i < boardTiles.size(); i++) {
            switch (boardTiles.get(i)) {
                case 'r':
                    builder.setPiece(new Rook(PlayerColor.BLACK, i));
                    continue;
                case 'n':
                    builder.setPiece(new Knight(PlayerColor.BLACK, i));
                    continue;
                case 'b':
                    builder.setPiece(new Bishop(PlayerColor.BLACK, i));
                    continue;
                case 'q':
                    builder.setPiece(new Queen(PlayerColor.BLACK, i));
                    continue;
                case 'k':
                    builder.setPiece(new King(PlayerColor.BLACK, i, blackKingSideCastle, blackQueenSideCastle));
                    continue;
                case 'p':
                    builder.setPiece(new Pawn(PlayerColor.BLACK, i));
                    continue;
                case 'R':
                    builder.setPiece(new Rook(PlayerColor.WHITE, i));
                    continue;
                case 'N':
                    builder.setPiece(new Knight(PlayerColor.WHITE, i));
                    continue;
                case 'B':
                    builder.setPiece(new Bishop(PlayerColor.WHITE, i));
                    continue;
                case 'Q':
                    builder.setPiece(new Queen(PlayerColor.WHITE, i));
                    continue;
                case 'K':
                    builder.setPiece(new King(PlayerColor.WHITE, i, whiteKingSideCastle, whiteQueenSideCastle));
                    continue;
                case 'P':
                    builder.setPiece(new Pawn(PlayerColor.WHITE, i));
                    continue;
                case '-':
                    continue;
                default:
                    throw new RuntimeException("Invalid FEN String " + gameConfiguration);
            }
        }
        builder.setMoveMaker(moveMaker(fenParts[1]));
        return builder.build();
    }

    private static PlayerColor moveMaker(String player) throws InvalidDataException {
        if (player.equals("w")) {
            return PlayerColor.WHITE;
        }
        if (player.equals("b")) {
            return PlayerColor.BLACK;
        }
        throw new InvalidDataException(ExceptionMessages.SYSTEM_ERROR_INVALID_DATA.getInfo());
    }

    private static boolean whiteKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("K");
    }

    private static boolean whiteQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("Q");
    }

    private static boolean blackKingSideCastle(String fenCastleString) {
        return fenCastleString.contains("k");
    }

    private static boolean blackQueenSideCastle(String fenCastleString) {
        return fenCastleString.contains("q");
    }

    private static String calculateCastleText(Board board) {
        StringBuilder builder = new StringBuilder();
        if (board.whitePlayer().isKingSideCastleCapable()) {
            builder.append("K");
        }
        if (board.whitePlayer().isQueenSideCastleCapable()) {
            builder.append("Q");
        }
        if (board.blackPlayer().isKingSideCastleCapable()) {
            builder.append("k");
        }
        if (board.blackPlayer().isQueenSideCastleCapable()) {
            builder.append("q");
        }
        String result = builder.toString();

        return result.isEmpty() ? "-" : result;
    }

    private static String calculatePassingField(Board board) {
        Pawn enPassantPawn = board.getPassingAttack();
        if (enPassantPawn != null) {
            return BoardService.getPositionAtCoordinate(enPassantPawn.getPosition() +
                    (8) * enPassantPawn.getPieceAllegiance().getOppositeDirection());
        }
        return "-";
    }

    private static String calculateBoardText(Board board) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardConfiguration.TILES_MAX; i++) {
            String tileText = board.getPiece(i) == null ? "-" :
                    board.getPiece(i).getPieceAllegiance().isWhite() ? board.getPiece(i).toString() :
                            board.getPiece(i).toString().toLowerCase();
            builder.append(tileText);
        }
        builder.insert(8, "/");
        builder.insert(17, "/");
        builder.insert(26, "/");
        builder.insert(35, "/");
        builder.insert(44, "/");
        builder.insert(53, "/");
        builder.insert(62, "/");
        return builder.toString()
                .replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    private static String calculateCurrentPlayerText(Board board) {
        return board.getCurrentPlayer().toString().substring(0, 1).toLowerCase();
    }


}

