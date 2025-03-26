/****************************************************************************
 * Copyright 2014 studio Aspix 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 ***************************************************************************/
package it.aspix.tabparser.main;

import it.aspix.tabparser.main.MessaggioErrore.GeneratoreErrore;

/****************************************************************************
 * Una classe in grado di visualizzare dei messaggi di errore
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public interface GestoreMessaggi {
	public void addMessaggio(MessaggioErrore me);
	public void rimuoviSelettivo(GeneratoreErrore g);
}
