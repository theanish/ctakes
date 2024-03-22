from ctakes_pbj.pbj_tools import pbj_defaults
from ctakes_pbj.pbj_tools.arg_parser import ArgParser


class PBJPipeline:

    def __init__(self):
        self.annotators = []
        self.initialized = False
        self.c_reader = None
        self.arg_parser = ArgParser()

    # Set the Collection Reader for the Corpus.
    # This is absolutely necessary.  If you don't tell the pipeline how to get the notes ...
    def reader(self, collection_reader):
        collection_reader.declare_params(self.arg_parser)
        collection_reader.set_pipeline(self)
        self.c_reader = collection_reader

    # Add an annotator to the pipeline.
    def add(self, cas_annotator):
        cas_annotator.declare_params(self.arg_parser)
        self.annotators.append(cas_annotator)

    # Fill command line parameters, then call each annotator to initialize.
    def initialize(self):
        if self.c_reader is None:
            print('No Reader Specified, quitting')
            exit(1)
        self.arg_parser.add_arg('-o', '--output_dir', default=pbj_defaults.DEFAULT_OUT_DIR)
        # Get/Init all of the declared parameter arguments.
        # Do the actual argument parsing.
        # If get_args has already been called then added parameters will crash the tool.
        args = self.arg_parser.get_args()
        # Set the necessary parameters in the collection reader.
        self.c_reader.init_params(args)
        # For each annotator set the necessary parameters.
        for annotator in self.annotators:
            annotator.init_params(args)
        # For each annotator initialize resources, etc.
        for annotator in self.annotators:
            annotator.initialize()
        self.initialized = True

    # Starts / Runs the pipeline.  This calls start on the collection reader.
    def run(self):
        if not self.initialized:
            self.initialize()
        self.c_reader.start()

    # For a new cas, call each annotator to process that cas.
    def process(self, cas):
        for annotator in self.annotators:
            annotator.process(cas)

    # At the end of the corpus, call each annotator for cleanup, etc.
    def collection_process_complete(self):
        for annotator in self.annotators:
            annotator.collection_process_complete()
