/* Generated By:JavaCC: Do not edit this line. ParserConstants.java */
// Copyright (c) 2013 Mikhail Afanasov and DeepSe group. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package parsers.component;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int CONTEXTGROUP = 9;
  /** RegularExpression Id. */
  int CONTEXT = 10;
  /** RegularExpression Id. */
  int CONFIGURATION = 11;
  /** RegularExpression Id. */
  int MODULE = 12;
  /** RegularExpression Id. */
  int INCLUDE = 13;
  /** RegularExpression Id. */
  int IMPLEMENTATION = 14;
  /** RegularExpression Id. */
  int LAYERED = 15;
  /** RegularExpression Id. */
  int EVENT = 16;
  /** RegularExpression Id. */
  int COMMAND = 17;
  /** RegularExpression Id. */
  int USES = 18;
  /** RegularExpression Id. */
  int PROVIDES = 19;
  /** RegularExpression Id. */
  int TRANSITION = 20;
  /** RegularExpression Id. */
  int TRIGGERS = 21;
  /** RegularExpression Id. */
  int INTERFACE = 22;
  /** RegularExpression Id. */
  int AS = 23;
  /** RegularExpression Id. */
  int IF = 24;
  /** RegularExpression Id. */
  int SEMICOLON = 25;
  /** RegularExpression Id. */
  int COMMA = 26;
  /** RegularExpression Id. */
  int OCB = 27;
  /** RegularExpression Id. */
  int CCB = 28;
  /** RegularExpression Id. */
  int ORB = 29;
  /** RegularExpression Id. */
  int CRB = 30;
  /** RegularExpression Id. */
  int INCLUDENAME = 31;
  /** RegularExpression Id. */
  int DIRECTORY = 32;
  /** RegularExpression Id. */
  int FULLNAME = 33;
  /** RegularExpression Id. */
  int LEXEME = 34;
  /** RegularExpression Id. */
  int NAME = 35;
  /** RegularExpression Id. */
  int STRING_LITERAL = 36;
  /** RegularExpression Id. */
  int CHAR = 37;
  /** RegularExpression Id. */
  int NUMBER = 38;
  /** RegularExpression Id. */
  int FLOAT = 39;
  /** RegularExpression Id. */
  int FLOATING_POINT_LITERAL = 40;
  /** RegularExpression Id. */
  int EXPONENT = 41;
  /** RegularExpression Id. */
  int OPERATION = 42;
  /** RegularExpression Id. */
  int ANY = 43;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int ML_COMMENT_STATE = 1;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "<token of kind 1>",
    "\"/*\"",
    "\" \"",
    "\"\\r\"",
    "\"\\t\"",
    "\"\\n\"",
    "\"*/\"",
    "<token of kind 8>",
    "\"context group\"",
    "\"context\"",
    "\"configuration\"",
    "\"module\"",
    "\"#include\"",
    "\"implementation\"",
    "\"layered\"",
    "\"event\"",
    "\"command\"",
    "\"uses\"",
    "\"provides\"",
    "\"transitions\"",
    "\"triggers\"",
    "\"interface\"",
    "\"as\"",
    "\"if\"",
    "\";\"",
    "\",\"",
    "\"{\"",
    "\"}\"",
    "\"(\"",
    "\")\"",
    "<INCLUDENAME>",
    "<DIRECTORY>",
    "<FULLNAME>",
    "<LEXEME>",
    "<NAME>",
    "<STRING_LITERAL>",
    "<CHAR>",
    "<NUMBER>",
    "<FLOAT>",
    "<FLOATING_POINT_LITERAL>",
    "<EXPONENT>",
    "<OPERATION>",
    "<ANY>",
  };

}
