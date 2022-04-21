/**
 * ================================================================
 *  Copyright (c) 2017-2018 Maiereni Software and Consulting Inc
 * ================================================================
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
 */
package com.maiereni.sample.files;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

/**
 * Sample class to process the content of a text file using stream 
 * @author Petre Maierean
 *
 */
public class ProcessingFiles {
	private static final Logger LOG = Logger.getLogger(ProcessingFiles.class); 

	/**
	 * Find the number of matches
	 * @param fileName the path to the text file
	 * @param regex the regular expression to match
	 * @return the number of matches
	 * @throws
	 * 
	 */
	public long getMatchesNumber(final String fileName, final String regex) throws IOException {
		Pattern pattern = Pattern.compile(regex);		
		return asStream(fileName)
			.filter( (s) -> pattern.matcher(s).matches() )
			.count();
	}
	
	/**
	 * Get all matches
	 * @param fileName the path to the text file
	 * @param regex the regular expression to match
	 * @return a list of matches
	 * @throws IOException
	 */
	public List<LineWithNumber> findAllMatchingLine(final String fileName, final String regex) throws IOException {
		Pattern pattern = Pattern.compile(regex);		
		return asStream(fileName)
			.map(new LineWithNumberFunction())
			.filter( (l) -> pattern.matcher(l.getString()).matches() )
			.collect( Collectors.toList() );
	}

	/**
	 * Get as optional
	 * @param fileName the path to the text file
	 * @param regex the regular expression to match
	 * @return the first match
	 * @throws IOException
	 */
	public Optional<LineWithNumber> findFirstMatchingLine(final String fileName, final String regex) throws IOException {
		Pattern pattern = Pattern.compile(regex);
		return asStream(fileName)
			.map(new LineWithNumberFunction())
			.filter( (l) -> pattern.matcher(l.getString()).matches() )
			.findFirst();
	}

	private Stream<String> asStream(final String fileName) throws IOException {
		Path p = FileSystems.getDefault().getPath(fileName);
		if (!p.toFile().exists())
			throw new IOException("The path does not exist");
		
		return Files.lines(p, Charset.defaultCharset());		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 2)
				throw new Exception("Expected arguments: text_file_name regex");
			Optional<LineWithNumber> on = new ProcessingFiles().findFirstMatchingLine(args[0], args[1]);
			on.ifPresent( (r) -> LOG.debug("Found at line " + r.getLineNumber() + "\r\n" + r.getString()));
		}
		catch(Exception e) {
			LOG.error("There was an error processing", e);
		}
	}
	
	private class LineWithNumber implements Serializable {
		private static final long serialVersionUID = 3877530337675630195L;
		private int lineNumber;
		private String s;
		public LineWithNumber(int count, final String s) {
			this.lineNumber = count;
			this.s = s;
		}
		public int getLineNumber() {
			return lineNumber;
		}
		public String getString() {
			return s;
		}
	}
	
	private class LineWithNumberFunction implements Function<String, LineWithNumber> {
		int count = 0;
		@Override
		public LineWithNumber apply(String t) {
			return new LineWithNumber(count++, t);
		}
	};

}
