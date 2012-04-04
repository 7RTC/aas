Alliance Accounting System
===================================

 /* 
  * Copyright (c) 2007-2012, AAS Contributors.
  * All rights reserved.
  *
  * Licensed under BSD 3-Clause License (http://www.opensource.org/licenses/BSD-3-Clause)
  * which can be found in the file LICENSE.txt at the root of this distribution.
  *
  */

AAS is an open-source multiplatform and multisystem accounting solution, primarily targeted at 
Portuguese-speaking users looking for a a simple financial accounting system for double-entry 
bookkeeping.

Modules: 

  * Chart of accounts
  * Journal entries
  * Cash book
  * Ledger
  * DRE
  * Assets and Finance Statements
  * Flow Charts
  * Balance Sheets

Technology: Java, Swing, JGoodies and HSQLDB. 

--------------------------------------------------------------------------

Maven build instructions:
   
  mvn clean package assembly:single
  The built JAR and it's required libraries will be in target/dist
