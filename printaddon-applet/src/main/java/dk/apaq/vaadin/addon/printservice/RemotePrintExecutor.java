/*
 * PrintService for Vaadin
 * Copyright (C) 2011 by Apaq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.apaq.vaadin.addon.printservice;

/**
 *
 * @author michael
 */
public interface RemotePrintExecutor {
    /**
     * Executes a remote print job.
     * @param service The Remote Print Service to use for printing.
     * @param data A byte array containing the zip file that holde the print job data.
     */
    void executeRemotePrint(RemotePrintService service, byte[] data);
    
    void executeRemotePrint(RemotePrintService service, byte[] data, String type);
}
